import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainpageController {

    @FXML
    private Button Btnhistory;

    @FXML
    private Button Btnsave;

    @FXML
    private Button Btnsend;

    @FXML
    private Button Btndeposit;

    @FXML
    private TextField deposittext;

    @FXML
    private Label labeluseramount;

    @FXML
    private Label labelusercardnum;

    @FXML
    private Label labelusercvv;

    @FXML
    private Label labeluserexpire;

    private String currentUsername;

    public void initialize() {
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadUserData();
    }

    private void loadUserData() {
        // Load user data from the database and set the labels
        String userInfoQuery = "SELECT balance FROM userinfotable WHERE username = ?";
        String debitCardQuery = "SELECT cardnumber, expires, cvv FROM debitcard WHERE accountnumber = (SELECT accountnumber FROM userinfotable WHERE username = ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement userInfoStmt = connection.prepareStatement(userInfoQuery);
             PreparedStatement debitCardStmt = connection.prepareStatement(debitCardQuery)) {

            userInfoStmt.setString(1, currentUsername);
            ResultSet userInfoResultSet = userInfoStmt.executeQuery();

            if (userInfoResultSet.next()) {
                double balance = userInfoResultSet.getDouble("balance");
                labeluseramount.setText(String.format("%.2f", balance));
            }

            debitCardStmt.setString(1, currentUsername);
            ResultSet debitCardResultSet = debitCardStmt.executeQuery();

            if (debitCardResultSet.next()) {
                String cardNumber = debitCardResultSet.getString("cardnumber");
                String expires = debitCardResultSet.getString("expires");
                String cvv = debitCardResultSet.getString("cvv");

                labelusercardnum.setText(cardNumber);
                labeluserexpire.setText(expires);
                labelusercvv.setText(cvv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btndepositHandler(ActionEvent event) throws IOException {
        double depositAmount;
        try {
            depositAmount = Double.parseDouble(deposittext.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid number.");
            alert.showAndWait();
            return;
        }

        String querySelect = "SELECT balance FROM userinfotable WHERE username = ?";
        String queryUpdate = "UPDATE userinfotable SET balance = ? WHERE username = ?";
        String depositQuery = "INSERT INTO deposits (accountnumber, deposit_amount, deposit_date) VALUES ((SELECT accountnumber FROM userinfotable WHERE username = ?), ?, ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmtSelect = connection.prepareStatement(querySelect);
             PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdate);
             PreparedStatement depositStmt = connection.prepareStatement(depositQuery)) {

            // Fetch current balance
            pstmtSelect.setString(1, currentUsername);
            ResultSet resultSet = pstmtSelect.executeQuery();
            double currentBalance = 0;
            if (resultSet.next()) {
                currentBalance = resultSet.getDouble("balance");
            }

            // Calculate new balance
            double newBalance = currentBalance + depositAmount;

            // Update balance in database
            pstmtUpdate.setDouble(1, newBalance);
            pstmtUpdate.setString(2, currentUsername);
            pstmtUpdate.executeUpdate();

            // Insert deposit record
            depositStmt.setString(1, currentUsername);
            depositStmt.setDouble(2, depositAmount);
            depositStmt.setTimestamp(3, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            depositStmt.executeUpdate();

            // Update label
            labeluseramount.setText(formatCurrency(newBalance));

            // Clear the deposit text field
            deposittext.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnsaveHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Gosave.fxml"));
        Parent savePage = loader.load();
        GosaveController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene saveScene = new Scene(savePage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(saveScene);
        window.show();
    }

    public void btnsendHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Send.fxml"));
        Parent sendPage = loader.load();

        // Get the controller and set the current username
        SendController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);

        Scene sendScene = new Scene(sendPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(sendScene);
        window.show();
    }

    public void btnuserHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("User.fxml"));
        Parent userPage = loader.load();

        // Get the controller and set the current username
        UserController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);

        Scene userScene = new Scene(userPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userScene);
        window.show();
    }

    public void btnhistoryHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Depohistory.fxml"));
        Parent historyPage = loader.load();
        DepohistoryController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene historyScene = new Scene(historyPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(historyScene);
        window.show();
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount).replace("$", "");
    }
}

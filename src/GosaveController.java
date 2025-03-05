import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class GosaveController {

    @FXML
    private Button Btnclaim;

    @FXML
    private Button Btnhistory;

    @FXML
    private Button Btnaddfunds;

    @FXML
    private Button Btnback;

    @FXML
    private TextField fieldadd;

    @FXML
    private Label labelgobalance;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String currentUsername;
    private String currentAccountName;
    private String currentAccountNumber;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadAccountName();
        loadAccountNumber();
        loadBalance();
    }

    public void initialize() {
    }

    private void loadAccountName() {
        String query = "SELECT accountname FROM userinfotable WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentUsername);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                currentAccountName = resultSet.getString("accountname");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAccountNumber() {
        String query = "SELECT accountnumber FROM userinfotable WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentUsername);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                currentAccountNumber = resultSet.getString("accountnumber");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBalance() {
        String query = "SELECT gobalance FROM userinfotable WHERE accountnumber = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentAccountNumber);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                double gobalance = resultSet.getDouble("gobalance");
                labelgobalance.setText(String.format("%.2f", gobalance));
            } else {
                labelgobalance.setText("0.00");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnbackHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Mainpage.fxml"));
        Parent userPage = loader.load();
        MainpageController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene userScene = new Scene(userPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userScene);
        window.show();
    }

    public void btnclaimHandler(ActionEvent event) throws IOException {
        String querySelect = "SELECT balance, gobalance FROM userinfotable WHERE username = ?";
        String queryUpdate = "UPDATE userinfotable SET balance = ?, gobalance = ? WHERE username = ?";
        String queryInsertClaimHistory = "INSERT INTO claimhistory (accountnumber, claim_amount, claim_date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmtSelect = connection.prepareStatement(querySelect);
             PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdate);
             PreparedStatement pstmtInsertClaimHistory = connection.prepareStatement(queryInsertClaimHistory)) {

            // Fetch current balance and gobalance
            pstmtSelect.setString(1, currentUsername);
            ResultSet resultSet = pstmtSelect.executeQuery();
            double currentBalance = 0;
            double currentGoBalance = 0;
            if (resultSet.next()) {
                currentBalance = resultSet.getDouble("balance");
                currentGoBalance = resultSet.getDouble("gobalance");
            }

            // Check if gobalance is 0
            if (currentGoBalance == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Funds");
                alert.setHeaderText(null);
                alert.setContentText("No funds in GO Save.");
                alert.showAndWait();
                return;
            }

            // Calculate new balance
            double newBalance = currentBalance + currentGoBalance;
            double newGoBalance = 0;

            // Update balance and gobalance in userinfotable
            pstmtUpdate.setDouble(1, newBalance);
            pstmtUpdate.setDouble(2, newGoBalance);
            pstmtUpdate.setString(3, currentUsername);
            pstmtUpdate.executeUpdate();

            // Insert into claimhistory
            java.sql.Timestamp currentDate = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
            pstmtInsertClaimHistory.setString(1, currentAccountNumber);
            pstmtInsertClaimHistory.setDouble(2, currentGoBalance);
            pstmtInsertClaimHistory.setTimestamp(3, currentDate);
            pstmtInsertClaimHistory.executeUpdate();

            // Update label
            loadBalance();

            // Show success alert
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Funds Claimed");
            alert.setHeaderText(null);
            alert.setContentText("Funds have been successfully claimed.");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnhistoryHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Godeposit.fxml"));
        Parent userPage = loader.load();
        GodepositController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene userScene = new Scene(userPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userScene);
        window.show();
    }

    public void btnaddfundsHandler(ActionEvent event) throws IOException {
        double addAmount;
        try {
            addAmount = Double.parseDouble(fieldadd.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid number.");
            alert.showAndWait();
            return;
        }

        String querySelect = "SELECT balance, gobalance FROM userinfotable WHERE username = ?";
        String queryUpdate = "UPDATE userinfotable SET balance = ?, gobalance = ? WHERE username = ?";
        String queryInsertGoBalanceDeposit = "INSERT INTO gobalancedeposit (accountnumber, deposit_amount, deposit_date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmtSelect = connection.prepareStatement(querySelect);
             PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdate);
             PreparedStatement pstmtInsertGoBalanceDeposit = connection.prepareStatement(queryInsertGoBalanceDeposit)) {

            // Fetch current balance and gobalance
            pstmtSelect.setString(1, currentUsername);
            ResultSet resultSet = pstmtSelect.executeQuery();
            double currentBalance = 0;
            double currentGoBalance = 0;
            if (resultSet.next()) {
                currentBalance = resultSet.getDouble("balance");
                currentGoBalance = resultSet.getDouble("gobalance");
            }

            // Check if there are sufficient funds
            if (currentBalance < addAmount) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Insufficient Funds");
                alert.setHeaderText(null);
                alert.setContentText("Insufficient funds.");
                alert.showAndWait();
                return;
            }

            // Calculate new balances
            double newBalance = currentBalance - addAmount;
            double newGoBalance = currentGoBalance + addAmount;

            // Update balance and gobalance in userinfotable
            pstmtUpdate.setDouble(1, newBalance);
            pstmtUpdate.setDouble(2, newGoBalance);
            pstmtUpdate.setString(3, currentUsername);
            pstmtUpdate.executeUpdate();

            // Insert into gobalancedeposit
            java.sql.Timestamp currentDate = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
            pstmtInsertGoBalanceDeposit.setString(1, currentAccountNumber);
            pstmtInsertGoBalanceDeposit.setDouble(2, addAmount);
            pstmtInsertGoBalanceDeposit.setTimestamp(3, currentDate);
            pstmtInsertGoBalanceDeposit.executeUpdate();

            // Update label
            loadBalance();

            // Clear the input field
            fieldadd.setText("");

            // goods
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Funds Added");
            alert.setHeaderText(null);
            alert.setContentText("Funds have been successfully added.");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserController {

    @FXML
    private Button Btnaddinfo;

    @FXML
    private Button Btnback;

    @FXML
    private Button Btndeleteuser;

    @FXML
    private Button Btnedit;

    @FXML
    private Button Btnlogout;

    @FXML
    private Label displayaccountnumber;

    @FXML
    private Label displaybirth;

    @FXML
    private Label displayemail;


    @FXML
    private Label displayname;

    @FXML
    private Label displayphonenum;

    @FXML
    private Label displayusername;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadUserData();
    }

    private void loadUserData() {
        String userInfoQuery = "SELECT accountnumber, balance, gobalance, birthday, email, accountname, userpassword, phonenumber, username FROM userinfotable WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement userInfoStmt = connection.prepareStatement(userInfoQuery)) {

            userInfoStmt.setString(1, currentUsername);
            ResultSet userInfoResultSet = userInfoStmt.executeQuery();

            if (userInfoResultSet.next()) {
                displayaccountnumber.setText(userInfoResultSet.getString("accountnumber"));
                displaybirth.setText(userInfoResultSet.getString("birthday"));
                displayemail.setText(userInfoResultSet.getString("email"));
                displayname.setText(userInfoResultSet.getString("accountname"));
                displayphonenum.setText(userInfoResultSet.getString("phonenumber"));
                displayusername.setText(userInfoResultSet.getString("username"));
            } else {
                System.out.println("No data found for username: " + currentUsername);
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

    public void btneditHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Useredit.fxml"));
        Parent userPage = loader.load();
        UsereditController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene userScene = new Scene(userPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userScene);
        window.show();
    }

    public void btnlogoutHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent loginPage = loader.load();
        Scene loginScene = new Scene(loginPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void btndeleteuserHandler(ActionEvent event) throws IOException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String deleteDebitCardQuery = "DELETE FROM debitcard WHERE accountnumber = (SELECT accountnumber FROM userinfotable WHERE username = ?)";
            String deleteGoBalanceDepositQuery = "DELETE FROM gobalancedeposit WHERE accountnumber = (SELECT accountnumber FROM userinfotable WHERE username = ?)";
            String deleteDepositsQuery = "DELETE FROM deposits WHERE accountnumber = (SELECT accountnumber FROM userinfotable WHERE username = ?)";
            String deleteTransactionHistoryQuery = "DELETE FROM transactionhistory WHERE fromaccount = (SELECT accountnumber FROM userinfotable WHERE username = ?) OR toaccount = (SELECT accountnumber FROM userinfotable WHERE username = ?)";
            String deleteUserQuery = "DELETE FROM userinfotable WHERE username = ?";

            try (Connection connection = DatabaseHandler.getDBConnection();
                 PreparedStatement deleteDebitCardStmt = connection.prepareStatement(deleteDebitCardQuery);
                 PreparedStatement deleteGoBalanceDepositStmt = connection.prepareStatement(deleteGoBalanceDepositQuery);
                 PreparedStatement deleteDepositsStmt = connection.prepareStatement(deleteDepositsQuery);
                 PreparedStatement deleteTransactionHistoryStmt = connection.prepareStatement(deleteTransactionHistoryQuery);
                 PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserQuery)) {

                // Delete from debitcard
                deleteDebitCardStmt.setString(1, currentUsername);
                deleteDebitCardStmt.executeUpdate();

                // Delete from gobalancedeposit
                deleteGoBalanceDepositStmt.setString(1, currentUsername);
                deleteGoBalanceDepositStmt.executeUpdate();

                // Delete from deposits
                deleteDepositsStmt.setString(1, currentUsername);
                deleteDepositsStmt.executeUpdate();

                // Delete from transactionhistory
                deleteTransactionHistoryStmt.setString(1, currentUsername);
                deleteTransactionHistoryStmt.setString(2, currentUsername);
                deleteTransactionHistoryStmt.executeUpdate();

                // Delete from userinfotable
                deleteUserStmt.setString(1, currentUsername);
                deleteUserStmt.executeUpdate();

                Alert confirmationAlert = new Alert(AlertType.INFORMATION);
                confirmationAlert.setTitle("Account Deleted");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Your account has been successfully deleted.");
                confirmationAlert.showAndWait();

                // Redirect to login page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent loginPage = loader.load();
                Scene loginScene = new Scene(loginPage);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(loginScene);
                window.show();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

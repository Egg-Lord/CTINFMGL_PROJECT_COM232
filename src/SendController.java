import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Random;

public class SendController {

    @FXML
    private Button Btnback;

    @FXML
    private Button Btnhistory;

    @FXML
    private Button Btnsenduser;

    @FXML
    private Label sendbalance;

    @FXML
    private TableColumn<UserInfo, String> accountnamecol;

    @FXML
    private TableColumn<UserInfo, String> accountnumcol;

    @FXML
    private TextField amounttext;

    @FXML
    private TableView<UserInfo> tableviewsend;

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadUserBalance();
        loadUserInfo();
    }

    @FXML
    public void initialize() {
        accountnamecol.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        accountnumcol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
    }

    private void loadUserInfo() {
        ObservableList<UserInfo> userInfoList = FXCollections.observableArrayList();
        String query = "SELECT accountname, accountnumber FROM userinfotable WHERE username != ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentUsername);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String accountName = resultSet.getString("accountname");
                String accountNumber = resultSet.getString("accountnumber");
                userInfoList.add(new UserInfo(accountName, accountNumber));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableviewsend.setItems(userInfoList);
    }

    private void loadUserBalance() {
        String query = "SELECT balance FROM userinfotable WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentUsername);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                sendbalance.setText(String.format("%.2f", balance));
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

    public void btnsenduserHandler(ActionEvent event) throws IOException {
        UserInfo selectedUser = tableviewsend.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Selection Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to send money to.");
            alert.showAndWait();
            return;
        }

        String amountStr = amounttext.getText();
        if (amountStr.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter an amount to send.");
            alert.showAndWait();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid amount.");
            alert.showAndWait();
            return;
        }

        if (amount <= 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Amount must be greater than zero.");
            alert.showAndWait();
            return;
        }

        String recipientAccountNumber = selectedUser.getAccountNumber();
        String senderUsername = currentUsername;

        // Validate senderUsername and recipientAccountName
        if (!senderUsername.matches("^[A-Za-z0-9]+$") || !selectedUser.getAccountName().matches("^[A-Za-z ]+$")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Usernames must contain only alphanumeric characters and spaces.");
            alert.showAndWait();
            return;
        }

        String getSenderBalanceQuery = "SELECT balance FROM userinfotable WHERE username = ?";
        String updateSenderBalanceQuery = "UPDATE userinfotable SET balance = balance - ? WHERE username = ?";
        String updateRecipientBalanceQuery = "UPDATE userinfotable SET balance = balance + ? WHERE accountnumber = ?";
        String insertTransactionQuery = "INSERT INTO transactionhistory (transactionID, fromaccount, toaccount, amount) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement getSenderBalanceStmt = connection.prepareStatement(getSenderBalanceQuery);
             PreparedStatement updateSenderBalanceStmt = connection.prepareStatement(updateSenderBalanceQuery);
             PreparedStatement updateRecipientBalanceStmt = connection.prepareStatement(updateRecipientBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Get sender's balance
            getSenderBalanceStmt.setString(1, senderUsername);
            ResultSet resultSet = getSenderBalanceStmt.executeQuery();
            if (resultSet.next()) {
                double senderBalance = resultSet.getDouble("balance");
                if (senderBalance < amount) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Balance Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Insufficient balance.");
                    alert.showAndWait();
                    return;
                }
            }

            // Get sender's account number
            String senderAccountNumber = "";
            String getSenderAccountNumberQuery = "SELECT accountnumber FROM userinfotable WHERE username = ?";
            try (PreparedStatement getSenderAccountNumberStmt = connection.prepareStatement(getSenderAccountNumberQuery)) {
                getSenderAccountNumberStmt.setString(1, senderUsername);
                ResultSet senderAccountNumberResultSet = getSenderAccountNumberStmt.executeQuery();
                if (senderAccountNumberResultSet.next()) {
                    senderAccountNumber = senderAccountNumberResultSet.getString("accountnumber");
                }
            }

            // Update sender's balance
            updateSenderBalanceStmt.setDouble(1, amount);
            updateSenderBalanceStmt.setString(2, senderUsername);
            updateSenderBalanceStmt.executeUpdate();

            // Update recipient's balance
            updateRecipientBalanceStmt.setDouble(1, amount);
            updateRecipientBalanceStmt.setString(2, recipientAccountNumber);
            updateRecipientBalanceStmt.executeUpdate();

            // Insert transaction into transactionhistory
            String transactionID = generateTransactionID();
            insertTransactionStmt.setString(1, transactionID);
            insertTransactionStmt.setString(2, senderAccountNumber);
            insertTransactionStmt.setString(3, recipientAccountNumber);
            insertTransactionStmt.setDouble(4, amount);
            insertTransactionStmt.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Transaction Successful");
            alert.setHeaderText(null);
            alert.setContentText("Amount sent successfully!");
            alert.showAndWait();

            amounttext.setText("");

            // Refresh the balance display
            loadUserBalance();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateTransactionID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void btnhistoryHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Transaction.fxml"));
        Parent transactionPage = loader.load();
        TransactionController transactionController = loader.getController();
        transactionController.setCurrentUsername(currentUsername);
        Scene transactionScene = new Scene(transactionPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(transactionScene);
        window.show();
    }
}
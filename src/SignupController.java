import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML
    private TextField entername;

    @FXML
    private TextField enterpassword;

    @FXML
    private TextField enteruser;

    @FXML
    private DatePicker enterbday;

    @FXML
    private TextField enteremail;

    @FXML
    private TextField enternumber;

    @FXML
    private Button Btnsignup;

    @FXML
    private Button Btnback;

    private Stage stage;
    private Scene scene;

    public void btnsignupHandler(ActionEvent event) throws IOException {
        String username = enteruser.getText();
        String password = enterpassword.getText();
        String accountName = entername.getText();
        LocalDate birthday = enterbday.getValue();
        String email = enteremail.getText();
        String phoneNumber = enternumber.getText();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || accountName.isEmpty() || birthday == null || email.isEmpty() || phoneNumber.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        // Generate required numbers and date
        String accountNumber = generateRandomNumber(12);
        String cardNumber = generateRandomNumber(16);
        String expires = generateExpiryDate();
        String cvv = generateRandomNumber(3);

        // Insert data into the userinfotable
        String userInfoQuery = "INSERT INTO userinfotable (username, userpassword, accountname, accountnumber, balance, gobalance, birthday, email, phonenumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Insert data into the gobalancedeposit table
        String goBalanceDepositQuery = "INSERT INTO gobalancedeposit (accountnumber, deposit_amount, deposit_date) VALUES (?, ?, ?)";

        // Insert data into the debitcard table
        String debitCardQuery = "INSERT INTO debitcard (accountnumber, cardnumber, expires, cvv) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement userInfoStmt = connection.prepareStatement(userInfoQuery);
             PreparedStatement goBalanceDepositStmt = connection.prepareStatement(goBalanceDepositQuery);
             PreparedStatement debitCardStmt = connection.prepareStatement(debitCardQuery)) {

            // Insert into userinfotable
            userInfoStmt.setString(1, username);
            userInfoStmt.setString(2, password);
            userInfoStmt.setString(3, accountName);
            userInfoStmt.setString(4, accountNumber);
            userInfoStmt.setDouble(5, 0.0); // balance
            userInfoStmt.setDouble(6, 0.0); // gobalance
            userInfoStmt.setString(7, birthday.toString());
            userInfoStmt.setString(8, email);
            userInfoStmt.setString(9, phoneNumber);
            userInfoStmt.executeUpdate();

            // Insert into gobalancedeposit
            goBalanceDepositStmt.setString(1, accountNumber);
            goBalanceDepositStmt.setDouble(2, 0.0); // initial go balance deposit
            goBalanceDepositStmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            goBalanceDepositStmt.executeUpdate();

            // Insert into debitcard
            debitCardStmt.setString(1, accountNumber);
            debitCardStmt.setString(2, cardNumber);
            debitCardStmt.setString(3, expires);
            debitCardStmt.setString(4, cvv);
            debitCardStmt.executeUpdate();

            // Clear input fields
            entername.setText("");
            enterpassword.setText("");
            enteruser.setText("");
            enterbday.setValue(null);
            enteremail.setText("");
            enternumber.setText("");

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateExpiryDate() {
        LocalDate now = LocalDate.now();
        LocalDate expiryDate = now.plusYears(3); // 3 years from now
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    public void btnbackHandler(ActionEvent event) throws IOException {
        Parent loginPage = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene loginScene = new Scene(loginPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
}
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UsereditController {

    @FXML
    private Button Btnback;

    @FXML
    private Button Btnemail;

    @FXML
    private Button Btnnum;

    @FXML
    private Button Btnpass;

    @FXML
    private TextField emailup;

    @FXML
    private TextField numup;

    @FXML
    private TextField passup;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        // Load user data or perform any necessary actions
    }

    @FXML
    public void btnbackHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("User.fxml"));
        Parent userPage = loader.load();
        UserController controller = loader.getController();
        controller.setCurrentUsername(currentUsername);
        Scene userScene = new Scene(userPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userScene);
        window.show();
    }

    @FXML
    public void btnemailHandler(ActionEvent event) {
        String newEmail = emailup.getText();
        if (!newEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address.");
            alert.showAndWait();
            return;
        }

        String query = "UPDATE userinfotable SET email = ? WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newEmail);
            pstmt.setString(2, currentUsername);
            pstmt.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Email Updated");
            alert.setHeaderText(null);
            alert.setContentText("Email has been successfully updated.");
            alert.showAndWait();
            emailup.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnnumHandler(ActionEvent event) {
        String newNumber = numup.getText();
        if (!newNumber.matches("^[0-9]{11}$")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Phone Number");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid 11-digit phone number.");
            alert.showAndWait();
            return;
        }

        String query = "UPDATE userinfotable SET phonenumber = ? WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newNumber);
            pstmt.setString(2, currentUsername);
            pstmt.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Phone Number Updated");
            alert.setHeaderText(null);
            alert.setContentText("Phone number has been successfully updated.");
            alert.showAndWait();
            numup.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnpassHandler(ActionEvent event) {
        String newPassword = passup.getText();
        if (!newPassword.matches("^[A-Za-z0-9]+$")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Password");
            alert.setHeaderText(null);
            alert.setContentText("Password can only contain letters and numbers.");
            alert.showAndWait();
            return;
        }

        String query = "UPDATE userinfotable SET userpassword = ? WHERE username = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, currentUsername);
            pstmt.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Password Updated");
            alert.setHeaderText(null);
            alert.setContentText("Password has been successfully updated.");
            alert.showAndWait();
            passup.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

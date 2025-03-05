import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoginController {

    @FXML
    private Button Btncreate;

    @FXML
    private Button Btnlogin;

    @FXML
    private TextField logintextusername;

    @FXML
    private TextField logintextpassword;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void btnloginHandler(ActionEvent event) throws IOException {
        String uname = logintextusername.getText();
        String pword = logintextpassword.getText();
        if (DatabaseHandler.validateLogin(uname, pword)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Mainpage.fxml"));
            root = loader.load();

            MainpageController mainpagecontroller = loader.getController();
            mainpagecontroller.setCurrentUsername(uname); // Pass the username to MainpageController

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
        }
    }

    @FXML
    public void btnsignupHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


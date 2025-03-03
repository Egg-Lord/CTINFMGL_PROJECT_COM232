import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TransactionController {

    @FXML
    private Button Btnback;

    @FXML
    private TableColumn<Transaction, String> amountcol;

    @FXML
    private TableColumn<Transaction, String> fromcol;

    @FXML
    private TableColumn<Transaction, String> idcol;

    @FXML
    private TableView<Transaction> tableviewhistory;

    @FXML
    private TableColumn<Transaction, String> tocol;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String currentUsername;
    private String currentAccountNumber;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadAccountNumber();
        loadTransactionHistory();
    }

    @FXML
    public void initialize() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        fromcol.setCellValueFactory(new PropertyValueFactory<>("fromAccount"));
        tocol.setCellValueFactory(new PropertyValueFactory<>("toAccount"));
        amountcol.setCellValueFactory(new PropertyValueFactory<>("amount"));
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

    private void loadTransactionHistory() {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        String query = "SELECT transactionID, fromaccount, toaccount, amount FROM transactionhistory WHERE fromaccount = ? OR toaccount = ?";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentAccountNumber);
            pstmt.setString(2, currentAccountNumber);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String transactionID = resultSet.getString("transactionID");
                String fromAccount = resultSet.getString("fromaccount");
                String toAccount = resultSet.getString("toaccount");
                double amount = resultSet.getDouble("amount");
                transactionList.add(new Transaction(transactionID, fromAccount, toAccount, amount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableviewhistory.setItems(transactionList);
    }

    public void btnbackHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Send.fxml"));
        Parent sendPage = loader.load();
        SendController sendController = loader.getController();
        sendController.setCurrentUsername(currentUsername);
        Scene sendScene = new Scene(sendPage);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(sendScene);
        window.show();
    }
}


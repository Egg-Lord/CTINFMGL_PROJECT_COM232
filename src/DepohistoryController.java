import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepohistoryController {

    @FXML
    private Button Btnback;

    @FXML
    private TableColumn<Deposit, Double> amountcol;

    @FXML
    private TableColumn<Deposit, String> datecol;

    @FXML
    private TableView<Deposit> historyview;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadDepositHistory();
    }

    @FXML
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

    private void loadDepositHistory() {
        ObservableList<Deposit> depositList = FXCollections.observableArrayList();
        String query = "SELECT deposit_amount, deposit_date FROM deposits WHERE accountnumber = (SELECT accountnumber FROM userinfotable WHERE username = ?)";

        try (Connection connection = DatabaseHandler.getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentUsername);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                double depositAmount = resultSet.getDouble("deposit_amount");
                String depositDate = resultSet.getString("deposit_date");
                depositList.add(new Deposit(depositAmount, depositDate));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        amountcol.setCellValueFactory(new PropertyValueFactory<>("depositAmount"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("depositDate"));
        historyview.setItems(depositList);
    }

    public static class Deposit {
        private final Double depositAmount;
        private final String depositDate;

        public Deposit(Double depositAmount, String depositDate) {
            this.depositAmount = depositAmount;
            this.depositDate = depositDate;
        }

        public Double getDepositAmount() {
            return depositAmount;
        }

        public String getDepositDate() {
            return depositDate;
        }
    }
}

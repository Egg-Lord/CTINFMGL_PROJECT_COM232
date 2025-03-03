import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    
    private static DatabaseHandler handler = null;
    private static Statement stmt = null;
    private static PreparedStatement pstatement = null;

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    public static Connection getDBConnection() {
        Connection connection = null;
        String dburl = "jdbc:mysql://localhost:3306/userdatabase?serverTimezone=Asia/Kuala_Lumpur";
        String userName = "root";
        String password = "Armored440";

        try {
            connection = DriverManager.getConnection(dburl, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = getDBConnection().createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        return result;
    }

    public static boolean validateLogin(String username, String userpassword) {
        getInstance();
        String query = "SELECT * FROM userinfotable WHERE username = ? AND userpassword = ?";
        ResultSet resultSet = null;

        try (Connection connection = getDBConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, userpassword);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}


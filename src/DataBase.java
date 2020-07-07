import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DataBase {

    private Map<String, Integer> data = new HashMap<>();
    private static Connection con;
    private static DataBase instance;

    private DataBase() {
        try {

            ResultSet resultSet = getWordsResultSet();

            while (resultSet.next()) {
                String[] wordArray = resultSet.getString("ii_i_title").split(" ");
                data.put(wordArray[0], 0);
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
            return instance;
        }

        return instance;
    }

    public Map<String, Integer> getData() {
        return data;
    }

    public ResultSet getWordsResultSet() throws SQLException, ClassNotFoundException {
        if (con == null) {
            getConnection();
        }

        assert con != null;
        Statement state = con.createStatement();
        return state.executeQuery("SELECT ii_i_id, ii_i_title FROM items_info");
    }

    private  void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:database.db");
    }

}

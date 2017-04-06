import java.sql.*;

/**
 * Created by yangyu on 16/12/30.
 */
public class TestDM {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String driver = "dm.jdbc.driver.DmDriver";
        String url = "jdbc:dm://10.5.4.100:5236";
        String userName = "SYSDBA";
        String passWord = "SYSDBA";

        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url,userName,passWord);
        Statement statement  = connection.createStatement();
        String sql = "SELECT * FROM SEEYON.TABLE1";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()){
            System.out.println(resultSet.getObject("name"));
        }
    }
}

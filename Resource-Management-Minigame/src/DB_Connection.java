import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Connection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/oop_project";
    private static final String USER = "postgres";
    private static final String PASS = "Pintecan";

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

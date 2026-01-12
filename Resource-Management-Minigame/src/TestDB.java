import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        try {
            // This line forces the driver to load for older Java versions
            Class.forName("org.postgresql.Driver");

            Connection conn = DB_Connection.get();
            if (conn != null) {
                System.out.println("✅ SUCCESS: Connected to PostgreSQL!");
                conn.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ DRIVER NOT FOUND: Add the JAR to your library!");
        } catch (SQLException e) {
            System.err.println("❌ CONNECTION FAILED: Check your password/username!");
            e.printStackTrace();
        }
    }
}
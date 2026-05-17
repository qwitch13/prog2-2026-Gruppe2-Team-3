package at.ac.fhcampuswien.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    // JDBC connection data for the H2 database.
    // The database file is stored in the user's home directory as "movieDb".
    private static final String JDBC_URL = "jdbc:h2:~/movieDb";
    private static final String USER = "user";
    private static final String PASSWORD = "pw";

    // Private constructor because this is a utility class and should not be instantiated.
    private DatabaseUtil() {}

    // Creates and returns a new connection to the H2 database.
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to database", e);
        }
    }

    // Creates the movies table if it does not already exist.
    // This method should be called once when the application starts.
    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS movies (
                id UUID PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                genre VARCHAR(100) NOT NULL,
                release_year INT NOT NULL
            );
            """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new DatabaseException("Could not initialize database", e);
        }
    }
}
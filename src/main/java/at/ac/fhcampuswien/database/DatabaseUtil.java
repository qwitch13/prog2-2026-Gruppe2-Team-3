package at.ac.fhcampuswien.database;

import at.ac.fhcampuswien.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    // jdbc connection data for the h2 database.
    // the database file is stored in src/data/movieDb.
    private static final String JDBC_URL = "jdbc:h2:./src/data/movieDb";
    private static final String USER = "user";
    private static final String PASSWORD = "pw";

    // utility class, no instances.
    private DatabaseUtil() {
    }

    // returns a fresh jdbc connection to the h2 database.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // creates the movies table if it does not exist yet.
    // surfaces any jdbc failure as a DatabaseException so callers can react.
    public static void initializeDatabase() throws DatabaseException {
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
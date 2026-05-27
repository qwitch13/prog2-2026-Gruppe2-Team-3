package at.ac.fhcampuswien.database;

import at.ac.fhcampuswien.EnvLoader;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.models.DummyMovieBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseUtil {
    static {
        EnvLoader.load();
    }

    private static final String JDBC_URL = "jdbc:h2:./src/data/movieDb";
    private static final String USER = System.getProperty("DB_USER", "user");
    private static final String PASSWORD = System.getProperty("DB_PASSWORD", "pw");

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void initializeDatabase() throws DatabaseException {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS movies (
                    id UUID PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    genre VARCHAR(100) NOT NULL,
                    release_year INT NOT NULL
                );
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createTableSql);

            if (isTableEmpty(connection)) {
                populateDummyData(connection);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not initialize database", e);
        }
    }

    private static boolean isTableEmpty(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM movies";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }

        return true;
    }

    private static void populateDummyData(Connection connection) throws SQLException {
        List<Movie> dummyMovies = DummyMovieBuilder.buildDummyMovies();
        String insertSql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Movie movie : dummyMovies) {
                statement.setObject(1, movie.getId());
                statement.setString(2, movie.getTitle());
                statement.setString(3, movie.getGenre());
                statement.setInt(4, movie.getReleaseYear());
                statement.addBatch();
            }

            statement.executeBatch();
            System.out.println("✓ Database populated with " + dummyMovies.size() + " dummy movies");
        }
    }
}
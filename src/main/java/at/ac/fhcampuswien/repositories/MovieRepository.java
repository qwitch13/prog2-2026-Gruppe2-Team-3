package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.database.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Repository class responsible for direct database access.
// This class contains the SQL statements and converts database rows into Movie objects.
public class MovieRepository {

    // Inserts a new movie into the database.
    public void add(Movie movie) throws DatabaseException{
        // SQL statement with placeholders.
        // The placeholders are filled later with the movie data.
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";

        // Open a database connection and prepare the SQL statement.
        // try-with-resources automatically closes the connection and statement afterwards.
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Fill the SQL placeholders with the movie values.
            // 1 = id, 2 = title, 3 = genre, 4 = release year
            statement.setObject(1, movie.getId());
            statement.setString(2, movie.getTitle());
            statement.setString(3, movie.getGenre());
            statement.setInt(4, movie.getReleaseYear());

            // Execute the INSERT statement.
            statement.executeUpdate();

        } catch (SQLException e) {
            // If a database error occurs, wrap it in a RuntimeException.
            throw new DatabaseException("Could not add movie", e);
        }
    }

    // Reads all movies from the database.
    public List<Movie> findAll() throws DatabaseException{
        // SQL statement to select all movie rows.
        String sql = "SELECT * FROM movies";

        // List that will store all movies found in the database.
        List<Movie> movies = new ArrayList<>();

        // Open a database connection, prepare the statement and execute the query.
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Go through every row in the result set.
            while (resultSet.next()) {
                // Read the database columns from the current row.
                UUID id = resultSet.getObject("id", UUID.class);
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                int releaseYear = resultSet.getInt("release_year");

                // Create a Movie object from the database values.
                Movie movie = new Movie(title, genre, releaseYear);

                // Set the database id on the Movie object.
                // This is important for update operations.
                movie.setId(id);

                // Add the movie to the result list.
                movies.add(movie);
            }

        } catch (SQLException e) {
            // If a database error occurs, wrap it in a RuntimeException.
            throw new DatabaseException("Could not find movies", e);
        }

        // Return the complete list of movies.
        return movies;
    }

    // Deletes a movie from the database by title, genre and release year.
    public boolean delete(Movie movie) throws MovieNotFoundException, DatabaseException {
        // SQL statement for deleting a movie.
        // The movie is identified by title, genre and release year.
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";

        // Open a database connection and prepare the DELETE statement.
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Fill the placeholders with the movie values.
            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());

            // Execute the DELETE statement.
            // rowsAffected contains the number of deleted rows.
            int rowsAffected = statement.executeUpdate();

            // Return true if at least one row was deleted.
            // Throw Movienotfound Error if no row was deleted
            if (rowsAffected <= 0){
                throw new MovieNotFoundException("Movie not found. Delete not possible");
            }
            return true;

        } catch (SQLException e) {
            // If a database error occurs, wrap it in a RuntimeException.
            throw new DatabaseException("Could not delete movie", e);
        }
    }

    // Updates an existing movie in the database.
    public boolean update(Movie movie) throws MovieNotFoundException, DatabaseException {
        // SQL statement for updating title, genre and release year.
        // The movie is identified by its id.
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";

        // Open a database connection and prepare the UPDATE statement.
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Fill the placeholders with the updated movie values.
            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());

            // The id is used in the WHERE condition to find the correct movie.
            statement.setObject(4, movie.getId());

            // Execute the UPDATE statement.
            // rowsAffected contains the number of updated rows.
            int rowsAffected = statement.executeUpdate();

            // Return true if at least one row was updated.
            // Throw Movienotfound error if no row was affected
            if (rowsAffected <= 0){
                throw new MovieNotFoundException("Movie not found. Update not possible");
            }
            return true;

        } catch (SQLException e) {
            // If a database error occurs, wrap it in a RuntimeException.
            throw new DatabaseException("Could not update movie", e);
        }
    }
}
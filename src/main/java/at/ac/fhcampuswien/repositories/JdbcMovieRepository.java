package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// jdbc-backed implementation of MovieRepository.
// every method that talks to jdbc translates a SQLException into a DatabaseException
// so callers do not have to know about jdbc specifics. methods that expect to find
// a row (delete, update) raise MovieNotFoundException when the row is missing.
//
// renamed from MovieRepository in exercise 4: the original name is now an
// interface, and the concrete jdbc implementation lives behind it. higher
// layers depend only on the MovieRepository interface (dependency inversion).
public class JdbcMovieRepository implements MovieRepository {

    // inserts a new movie into the database.
    // throws DatabaseException if the underlying jdbc call fails.
    @Override
    public void add(Movie movie) throws DatabaseException {
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, movie.getId());
            statement.setString(2, movie.getTitle());
            statement.setString(3, movie.getGenre());
            statement.setInt(4, movie.getReleaseYear());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not add movie", e);
        }
    }

    // reads all movies from the database.
    // throws DatabaseException if the query fails.
    @Override
    public List<Movie> findAll() throws DatabaseException {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UUID id = resultSet.getObject("id", UUID.class);
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                int releaseYear = resultSet.getInt("release_year");

                Movie movie = new Movie(title, genre, releaseYear);
                movie.setId(id);

                movies.add(movie);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not find movies", e);
        }

        return movies;
    }

    // deletes a movie matched by title, genre and release year.
    // throws MovieNotFoundException if no row was deleted.
    // throws DatabaseException if the jdbc call fails.
    @Override
    public boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Movie not found for deletion");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Could not delete movie", e);
        }
    }

    // updates the title, genre and release year of a movie identified by id.
    // throws MovieNotFoundException if no row matched the id.
    // throws DatabaseException if the jdbc call fails.
    @Override
    public boolean update(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());
            statement.setObject(4, movie.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Movie not found for update");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Could not update movie", e);
        }
    }
}

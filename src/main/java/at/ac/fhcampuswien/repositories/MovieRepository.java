package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.models.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieRepository {

    public void add(Movie movie) {
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, movie.getId());
            statement.setString(2, movie.getTitle());
            statement.setString(3, movie.getGenre());
            statement.setInt(4, movie.getReleaseYear());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Could not add movie", e);
        }
    }

    public List<Movie> findAll() {
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
            throw new RuntimeException("Could not find movies", e);
        }

        return movies;
    }

    public boolean delete(Movie movie) {
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not delete movie", e);
        }
    }

    public boolean update(Movie movie) {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());
            statement.setObject(4, movie.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not update movie", e);
        }
    }
}
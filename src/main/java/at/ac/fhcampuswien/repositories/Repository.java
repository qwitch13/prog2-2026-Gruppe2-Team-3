package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import java.util.List;


public interface Repository {
    void add(Movie movie) throws DatabaseException;

    // Reads all movies from the database.
    List<Movie> findAll() throws DatabaseException;

    // Deletes a movie from the database by title, genre and release year.
    boolean delete(Movie movie) throws MovieNotFoundException, DatabaseException;

    // Updates an existing movie in the database.
    boolean update(Movie movie) throws MovieNotFoundException, DatabaseException;

}

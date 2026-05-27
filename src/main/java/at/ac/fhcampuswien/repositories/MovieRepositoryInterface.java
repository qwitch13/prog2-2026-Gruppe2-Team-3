package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.database.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

/**
 * Interface for movie repository operations.
 *
 * This interface defines the database operations that the service layer needs.
 * The service does not need to know how the data is stored internally.
 */
public interface MovieRepositoryInterface {

    /**
     * Adds a new movie.
     *
     * @param movie movie to add
     * @throws DatabaseException if a database error occurs
     */
    void add(Movie movie) throws DatabaseException;

    /**
     * Returns all movies.
     *
     * @return list of all movies
     * @throws DatabaseException if a database error occurs
     */
    List<Movie> findAll() throws DatabaseException;

    /**
     * Deletes a movie.
     *
     * @param movie movie to delete
     * @return true if the movie was deleted
     * @throws MovieNotFoundException if the movie does not exist
     * @throws DatabaseException if a database error occurs
     */
    boolean delete(Movie movie) throws MovieNotFoundException, DatabaseException;

    /**
     * Updates an existing movie.
     *
     * @param movie movie with updated values
     * @return true if the movie was updated
     * @throws MovieNotFoundException if the movie does not exist
     * @throws DatabaseException if a database error occurs
     */
    boolean update(Movie movie) throws MovieNotFoundException, DatabaseException;
}
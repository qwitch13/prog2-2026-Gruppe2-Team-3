package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.database.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;

/**
 * Service class for managing movie-related business logic.
 *
 * The service layer should not contain SQL code.
 * Database operations are delegated to MovieRepository.
 */
public class MovieService {

    private final MovieRepository movieRepository;

    /**
     * Constructor injection:
     * MovieRepository is provided from outside instead of being created inside the service.
     *
     * @param movieRepository repository used for database access
     */
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Returns all movies.
     *
     * The service delegates the database access to the repository.
     *
     * @return list of all movies
     * @throws DatabaseException if a database error occurs
     */
    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    /**
     * Searches movies by title, genre and release year.
     *
     * The search is case-insensitive for title and genre.
     * It also works with partial strings.
     *
     * @param title title or part of the title
     * @param genre genre or part of the genre
     * @param releaseYear release year as string
     * @return list of matching movies
     * @throws DatabaseException if movies cannot be loaded from the database
     */
    public List<Movie> searchMovies(String title, String genre, String releaseYear) throws DatabaseException {
        return movieRepository.findAll().stream()
                .filter(movie -> title == null || title.isEmpty()
                        || movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(movie -> genre == null || genre.isEmpty()
                        || movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(movie -> releaseYear == null || releaseYear.isEmpty()
                        || String.valueOf(movie.getReleaseYear()).contains(releaseYear))
                .toList();
    }

    /**
     * Checks if a movie already exists.
     *
     * A movie is considered equal if title, genre and release year match.
     *
     * @param movie movie to check
     * @return true if the movie already exists, false otherwise
     * @throws DatabaseException if movies cannot be loaded from the database
     */
    public boolean movieExists(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.findAll().stream()
                .anyMatch(m -> m.getTitle().equals(movie.getTitle())
                        && m.getGenre().equals(movie.getGenre())
                        && m.getReleaseYear() == movie.getReleaseYear());
    }

    /**
     * Adds a movie after a basic validation.
     *
     * @param movie movie to add
     * @return true if the movie was added, false if the movie data is invalid
     * @throws DatabaseException if the movie cannot be saved in the database
     */
    public boolean addMovie(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        movieRepository.add(movie);
        return true;
    }

    /**
     * Deletes a movie.
     *
     * @param movie movie to delete
     * @return true if the movie was deleted, false if the movie data is invalid
     * @throws DatabaseException if a database error occurs
     * @throws MovieNotFoundException if the movie does not exist
     */
    public boolean deleteMovie(Movie movie) throws DatabaseException, MovieNotFoundException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.delete(movie);
    }

    /**
     * Updates an existing movie.
     *
     * @param updatedMovie movie with updated values
     * @return true if the movie was updated, false if the movie data is invalid
     * @throws DatabaseException if a database error occurs
     * @throws MovieNotFoundException if the movie does not exist
     */
    public boolean updateMovie(Movie updatedMovie) throws MovieNotFoundException, DatabaseException {
        if (updatedMovie == null || updatedMovie.getId() == null) {
            return false;
        }

        return movieRepository.update(updatedMovie);
    }
}
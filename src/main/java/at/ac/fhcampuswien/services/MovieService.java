/** updated for ex3 **/
package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;

/**
 * Service class for managing movie business logic.
 *
 * In Exercise 2 this class worked with an in-memory List<Movie>.
 * In Exercise 3 it was refactored to use MovieRepository instead.
 *
 * The service layer contains the business logic.
 * The repository layer handles the direct database access.
 */
public class MovieService {

    private final MovieRepository movieRepository;

    /**
     * Constructor for MovieService with dependency injection of MovieRepository.
     *
     * @param movieRepository the repository used for database operations
     */
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Returns the complete list of movies.
     *
     * The movies are now loaded from the database through MovieRepository.
     *
     * @return list of all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Searches movies by title, genre and release year.
     *
     * The search is case-insensitive for title and genre.
     * It also works with partial strings.
     *
     * Example:
     * title = "incep" can find "Inception".
     *
     * @param title title or part of the title
     * @param genre genre or part of the genre
     * @param releaseYear release year as string
     * @return list of matching movies
     */
    public List<Movie> searchMovies(String title, String genre, String releaseYear) {
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
     * Checks if a movie already exists by title, genre and release year.
     *
     * The method loads all movies from the repository and checks if one movie
     * has the same title, genre and release year.
     *
     * @param movie the movie to check
     * @return true if movie exists, false otherwise
     */
    public boolean movieExists(Movie movie) {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.findAll().stream()
                .anyMatch(m -> m.getTitle().equals(movie.getTitle())
                        && m.getGenre().equals(movie.getGenre())
                        && m.getReleaseYear() == movie.getReleaseYear());
    }

    /**
     * Adds a new movie to the database.
     *
     * The service checks if the movie is valid.
     * The actual database insert is done by MovieRepository.
     *
     * @param movie the movie to add
     * @return true if movie was added, false otherwise
     */
    public boolean addMovie(Movie movie) {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        movieRepository.add(movie);
        return true;
    }

    /**
     * Deletes a movie from the database.
     *
     * The movie is identified by title, genre and release year.
     * The actual database delete operation is done by MovieRepository.
     *
     * @param movie the movie to delete
     * @return true if movie was deleted, false if not found
     */
    public boolean deleteMovie(Movie movie) {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.delete(movie);
    }

    /**
     * Updates an existing movie in the database.
     *
     * The movie is identified by its id.
     * The actual database update operation is done by MovieRepository.
     *
     * @param updatedMovie the movie with updated values
     * @return true if movie was updated, false if not found
     */
    public boolean updateMovie(Movie updatedMovie) {
        if (updatedMovie == null || updatedMovie.getId() == null) {
            return false;
        }

        return movieRepository.update(updatedMovie);
    }
} // **updated for exercise 3**/
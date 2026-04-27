/** created for ex2  **/
package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;
import java.util.Optional;

/**
 * service class for managing movie business logic.
 * handles crud operations for movie entities using java streams and lambda expressions.
 */
public class MovieService {
    private final List<Movie> movies;

    /**
     * Gibt die gesamte Liste der Filme zurück.
     */
    public List<Movie> getAllMovies() {
        return movies;
    }

    /**
     * constructor for movieservice with dependency injection of movies list.
     *
     * @param movies the list of movies to manage
     */
    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    /**
     * searches movies by title, genre and release year.
     * Search is case-insensitive for title and genre and also works with partial strings.
     *
     * @param title title or part of the title
     * @param genre genre or part of the genre
     * @param releaseYear release year as string
     * @return list of matching movies
     */
    public List<Movie> searchMovies(String title, String genre, String releaseYear) {
        return movies.stream()
                .filter(movie -> title == null || title.isEmpty()
                        || movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(movie -> genre == null || genre.isEmpty()
                        || movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(movie -> releaseYear == null || releaseYear.isEmpty()
                        || String.valueOf(movie.getReleaseYear()).contains(releaseYear))
                .toList();
    }
    /**
     * checks if a movie already exists by title, genre, and release year.
     * uses stream api to find matching movie.
     *
     * @param movie the movie to check
     * @return true if movie exists, false otherwise
     */
    public boolean movieExists(Movie movie) {
        return movies.stream()
                .anyMatch(m -> m.getTitle().equals(movie.getTitle())
                        && m.getGenre().equals(movie.getGenre())
                        && m.getReleaseYear() == movie.getReleaseYear());
    }

    /**
     * adds a new movie to the list.
     *
     * @param movie the movie to add
     */
    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    /**
     * deletes a movie by title, genre, and release year.
     * uses removeif with stream-compatible lambda expression.
     *
     * @param movie the movie to delete (identified by title, genre, releaseYear)
     * @return true if movie was deleted, false if not found
     */
    public boolean deleteMovie(Movie movie) {
        return movies.removeIf(m -> m.getTitle().equals(movie.getTitle())
                && m.getGenre().equals(movie.getGenre())
                && m.getReleaseYear() == movie.getReleaseYear());
    }

    /**
     * updates a movie by its id with new values.
     * uses stream api to find the movie by id.
     *
     * @param updatedMovie the movie with updated values (must include id)
     * @return true if movie was updated, false if not found
     */
    public boolean updateMovie(Movie updatedMovie) {
        Optional<Movie> existingMovie = movies.stream()
                .filter(m -> m.getId().equals(updatedMovie.getId()))
                .findFirst();

        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            movie.setTitle(updatedMovie.getTitle());
            movie.setGenre(updatedMovie.getGenre());
            movie.setReleaseYear(updatedMovie.getReleaseYear());
            return true;
        }

        return false;
    }
} //**created for exercise 2**/

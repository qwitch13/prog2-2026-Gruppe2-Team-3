package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;

// service class with the movie business logic.
// in ex2 it owned an in-memory List<Movie>. in ex3 it delegates every
// persistence concern to MovieRepository and only handles validation and
// search filtering on top of what the repository returns.
//
// the service never catches DatabaseException or MovieNotFoundException;
// both are propagated up to the controller which maps them to http codes.
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // returns every movie currently stored in the database.
    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    // case-insensitive partial-match search across title, genre and release year.
    // empty or null parameters act as wildcards.
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

    // checks whether a movie with the same title, genre and release year already exists.
    // used by the controller before inserting to avoid duplicates.
    public boolean movieExists(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.findAll().stream()
                .anyMatch(m -> m.getTitle().equals(movie.getTitle())
                        && m.getGenre().equals(movie.getGenre())
                        && m.getReleaseYear() == movie.getReleaseYear());
    }

    // inserts a new movie via the repository. returns false for obviously invalid input
    // so the controller can answer with 400 without having to talk to the database.
    public boolean addMovie(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        movieRepository.add(movie);
        return true;
    }

    // deletes a movie identified by title, genre and release year.
    // throws MovieNotFoundException if nothing matched the criteria.
    public boolean deleteMovie(Movie movie) throws DatabaseException, MovieNotFoundException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return movieRepository.delete(movie);
    }

    // updates an existing movie identified by id.
    // throws MovieNotFoundException if the id is not present in the database.
    public boolean updateMovie(Movie updatedMovie) throws DatabaseException, MovieNotFoundException {
        if (updatedMovie == null || updatedMovie.getId() == null) {
            return false;
        }

        return movieRepository.update(updatedMovie);
    }
}

package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.Repository;

import java.util.List;

public class MovieService {

    // SOLID - Dependency Inversion Principle
    // Repository Interface & Movie Repository now injected and dependency inverse
    // all movieRepository -> Repository
    private final Repository repository;

    public MovieService(Repository repository) {
        this.repository = repository;
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return repository.findAll();
    }

    public List<Movie> searchMovies(String title, String genre, String releaseYear) throws DatabaseException {
        return repository.findAll().stream()
                .filter(movie -> title == null || title.isEmpty()
                        || movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(movie -> genre == null || genre.isEmpty()
                        || movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(movie -> releaseYear == null || releaseYear.isEmpty()
                        || String.valueOf(movie.getReleaseYear()).contains(releaseYear))
                .toList();
    }

    public boolean movieExists(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return repository.findAll().stream()
                .anyMatch(m -> m.getTitle().equals(movie.getTitle())
                        && m.getGenre().equals(movie.getGenre())
                        && m.getReleaseYear() == movie.getReleaseYear());
    }

    public boolean addMovie(Movie movie) throws DatabaseException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        repository.add(movie);
        return true;
    }

    public boolean deleteMovie(Movie movie) throws DatabaseException, MovieNotFoundException {
        if (movie == null || movie.getTitle() == null) {
            return false;
        }

        return repository.delete(movie);
    }

    public boolean updateMovie(Movie updatedMovie) throws MovieNotFoundException, DatabaseException {
        if (updatedMovie == null || updatedMovie.getId() == null) {
            return false;
        }

        return repository.update(updatedMovie);
    }
}

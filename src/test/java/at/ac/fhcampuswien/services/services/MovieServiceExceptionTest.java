package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// dedicated tests that exercise the exception propagation paths
// for MovieNotFoundException and DatabaseException through the service layer.
// the repository is mocked with Mockito, so these tests run without any database.
class MovieServiceExceptionTest {

    private MovieService movieService;
    private MovieRepository movieRepository;

    @BeforeEach
    void set_up() throws DatabaseException {
        movieRepository = mock(MovieRepository.class);

        List<Movie> movies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("The Dark Knight", "Action", 2008),
                new Movie("Interstellar", "Sci-Fi", 2014)
        ));

        when(movieRepository.findAll()).thenReturn(movies);

        movieService = new MovieService(movieRepository);
    }

    // verifies that a DatabaseException raised by the repository on delete
    // is propagated unchanged by the service layer.
    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error()
            throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);
        when(movieRepository.delete(movieToDelete))
                .thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    // verifies that a MovieNotFoundException raised by the repository on delete
    // (no row matched) is propagated unchanged by the service layer.
    @Test
    void should_throw_movie_not_found_when_deleting_unknown_movie()
            throws DatabaseException, MovieNotFoundException {
        Movie unknown = new Movie("Unknown", "Drama", 1900);
        when(movieRepository.delete(unknown))
                .thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(unknown));
    }

    // verifies that updating a movie whose id is not in the database surfaces
    // a MovieNotFoundException to the caller.
    @Test
    void should_throw_movie_not_found_when_updating_unknown_movie()
            throws DatabaseException, MovieNotFoundException {
        Movie missing = new Movie("Ghost", "Horror", 1999);
        missing.setId(UUID.randomUUID());
        when(movieRepository.update(missing))
                .thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(missing));
    }

    // verifies that a DatabaseException raised by the repository on findAll
    // is propagated by both getAllMovies and searchMovies.
    @Test
    void should_throw_database_exception_when_listing_movies_fails() throws DatabaseException {
        when(movieRepository.findAll()).thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.getAllMovies());
        assertThrows(DatabaseException.class, () -> movieService.searchMovies("a", "b", "c"));
    }
}

package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void set_up() throws DatabaseException {
        movieRepository = mock(MovieRepository.class);

        List<Movie> movies = new ArrayList<>(Arrays.asList(
                new Movie.MovieBuilder("Inception")
                        .genre("Sci-Fi")
                        .releaseYear(2010)
                        .build(),
                new Movie.MovieBuilder("The Dark Knight")
                        .genre("Action")
                        .releaseYear(2008)
                        .build(),
                new Movie.MovieBuilder("Interstellar")
                        .genre("Sci-Fi")
                        .releaseYear(2014)
                        .build()
        ));

        when(movieRepository.findAll()).thenReturn(movies);

        movieService = new MovieService(movieRepository);
    }

    // verifies that a DatabaseException raised by the repository on delete
    // is propagated unchanged by the service layer.
    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error()
            throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie.MovieBuilder("Inception").genre("Sci-Fi").releaseYear(2010).build();
        when(movieRepository.delete(movieToDelete))
                .thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    // verifies that a MovieNotFoundException raised by the repository on delete
    // (no row matched) is propagated unchanged by the service layer.
    @Test
    void should_throw_movie_not_found_when_deleting_unknown_movie()
            throws DatabaseException, MovieNotFoundException {
        Movie unknown = new Movie.MovieBuilder("Unknown").genre("Drama").releaseYear(1900).build();
        when(movieRepository.delete(unknown))
                .thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(unknown));
    }

    // verifies that updating a movie whose id is not in the database surfaces
    // a MovieNotFoundException to the caller.
    @Test
    void should_throw_movie_not_found_when_updating_unknown_movie()
            throws DatabaseException, MovieNotFoundException {
        Movie missing = new Movie.MovieBuilder("Ghost").genre("Horror").releaseYear(1999).build();
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

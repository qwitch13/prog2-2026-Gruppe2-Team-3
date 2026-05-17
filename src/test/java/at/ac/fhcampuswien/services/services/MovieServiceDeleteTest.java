package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.database.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// delete-related tests for MovieService.
// the repository is mocked; "successful" delete is simulated by stubbing
// repository.delete() to return true, and "not found" by throwing MovieNotFoundException.
public class MovieServiceDeleteTest {

    private MovieService movieService;
    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        movieService = new MovieService(movieRepository);
    }

    @Test
    void givenCorrectMovie_whenDelete_deletesMovie() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);
        when(movieRepository.delete(movieToDelete)).thenReturn(true);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertTrue(result);
        verify(movieRepository).delete(movieToDelete);
    }

    @Test
    void givenNonExistingMovie_whenDelete_throwsMovieNotFoundException() throws DatabaseException, MovieNotFoundException {
        Movie nonExistent = new Movie("Avatar", "Sci-Fi", 2009);
        when(movieRepository.delete(nonExistent))
                .thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(nonExistent));
    }

    @Test
    void givenNullMovie_whenDelete_returnsFalse() throws DatabaseException, MovieNotFoundException {
        boolean result = movieService.deleteMovie(null);

        assertFalse(result);
        verify(movieRepository, never()).delete(any());
    }

    @Test
    void givenMovieWithNullTitle_whenDelete_returnsFalse() throws DatabaseException, MovieNotFoundException {
        Movie incomplete = new Movie(null, "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(incomplete);

        assertFalse(result);
        verify(movieRepository, never()).delete(any());
    }

    @Test
    void givenDatabaseError_whenDelete_propagatesDatabaseException() throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        when(movieRepository.delete(movie))
                .thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movie));
    }
}

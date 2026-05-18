package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// update-related tests for MovieService. all repository calls are mocked.
public class MovieServiceUpdateTest {

    private MovieService movieService;
    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        movieService = new MovieService(movieRepository);
    }

    @Test
    void testUpdateMovie_Success() throws DatabaseException, MovieNotFoundException {
        Movie updated = new Movie("Inception Reborn", "Sci-Fi", 2010);
        updated.setId(UUID.randomUUID());
        when(movieRepository.update(updated)).thenReturn(true);

        boolean result = movieService.updateMovie(updated);

        assertTrue(result);
        verify(movieRepository).update(updated);
    }

    @Test
    void testUpdateMovie_NotFound_ThrowsException() throws DatabaseException, MovieNotFoundException {
        Movie missing = new Movie("Titanic", "Drama", 1997);
        missing.setId(UUID.randomUUID());
        when(movieRepository.update(missing))
                .thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(missing));
    }

    @Test
    void testUpdateMovie_NullMovie_ReturnsFalse() throws DatabaseException, MovieNotFoundException {
        boolean result = movieService.updateMovie(null);

        assertFalse(result);
        verify(movieRepository, never()).update(any());
    }

    @Test
    void testUpdateMovie_NullId_ReturnsFalse() throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        movie.setId(null);

        boolean result = movieService.updateMovie(movie);

        assertFalse(result);
        verify(movieRepository, never()).update(any());
    }

    @Test
    void testUpdateMovie_PropagatesDatabaseException() throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        movie.setId(UUID.randomUUID());
        when(movieRepository.update(movie))
                .thenThrow(new DatabaseException("connection lost"));

        assertThrows(DatabaseException.class, () -> movieService.updateMovie(movie));
    }
}

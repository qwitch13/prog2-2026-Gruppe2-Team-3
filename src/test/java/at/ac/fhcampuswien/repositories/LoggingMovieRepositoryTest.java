package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// verifies the decorator pattern: every call on LoggingMovieRepository
// must be forwarded to the wrapped MovieRepository unchanged.
class LoggingMovieRepositoryTest {

    private MovieRepository delegate;
    private LoggingMovieRepository decorator;

    @BeforeEach
    void setUp() {
        delegate = mock(MovieRepository.class);
        decorator = new LoggingMovieRepository(delegate);
    }

    @Test
    void findAllDelegates() throws DatabaseException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        when(delegate.findAll()).thenReturn(List.of(movie));

        List<Movie> result = decorator.findAll();

        assertEquals(1, result.size());
        verify(delegate).findAll();
    }

    @Test
    void addDelegates() throws DatabaseException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);

        decorator.add(movie);

        verify(delegate).add(movie);
    }

    @Test
    void deleteDelegates() throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        when(delegate.delete(movie)).thenReturn(true);

        boolean result = decorator.delete(movie);

        assertEquals(true, result);
        verify(delegate).delete(movie);
    }

    @Test
    void updateDelegates() throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        when(delegate.update(movie)).thenReturn(true);

        boolean result = decorator.update(movie);

        assertEquals(true, result);
        verify(delegate).update(movie);
    }

    @Test
    void exceptionsBubbleUpUnchanged() throws DatabaseException, MovieNotFoundException {
        when(delegate.delete(any(Movie.class)))
                .thenThrow(new MovieNotFoundException("nope"));

        try {
            decorator.delete(new Movie("Ghost", "Horror", 1999));
            org.junit.jupiter.api.Assertions.fail("expected MovieNotFoundException");
        } catch (MovieNotFoundException expected) {
            assertEquals("nope", expected.getMessage());
        }
    }
}
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// add-related tests for MovieService.
// MovieRepository is mocked so the test never touches the real h2 database.
public class MovieServiceAddTest {

    private MovieService movieService;
    @Mock
    private MovieRepository movieRepository;
    private List<Movie> movies;

    @BeforeEach
    void setUp() throws DatabaseException, MovieNotFoundException {
        movieRepository = mock(MovieRepository.class);

        movies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("The Dark Knight", "Action", 2008),
                new Movie("Interstellar", "Sci-Fi", 2014)
        ));

        // every findAll() returns the same in-memory list; add() appends to it
        // so we can keep the existing "list grows by one" style of assertions.
        when(movieRepository.findAll()).thenReturn(movies);
        doAnswer(invocation -> {
            movies.add(invocation.getArgument(0));
            return null;
        }).when(movieRepository).add(any(Movie.class));

        movieService = new MovieService(movieRepository);
    }

    @Test
    void testAddMovie_Success() throws DatabaseException {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result, "Should return true when movie is added");
        verify(movieRepository, times(1)).add(newMovie);
        assertEquals(4, movies.size(), "Movie list should contain one more movie");
        assertTrue(movies.contains(newMovie));
    }

    @Test
    void testAddMovie_DelegatesToRepository() throws DatabaseException {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        // the only thing the service should do on a valid movie is call repository.add
        verify(movieRepository).add(newMovie);
    }

    @Test
    void testAddMovie_NullMovie_ReturnsFalse() throws DatabaseException {
        boolean result = movieService.addMovie(null);

        assertFalse(result);
        verify(movieRepository, never()).add(any());
    }

    @Test
    void testAddMovie_NullTitle_ReturnsFalse() throws DatabaseException {
        Movie incomplete = new Movie(null, "Drama", 1997);

        boolean result = movieService.addMovie(incomplete);

        assertFalse(result);
        verify(movieRepository, never()).add(any());
    }

    @Test
    void testAddMovie_MultipleMovies() throws DatabaseException {
        movieService.addMovie(new Movie("Titanic", "Drama", 1997));
        movieService.addMovie(new Movie("Avatar", "Sci-Fi", 2009));

        verify(movieRepository, times(2)).add(any(Movie.class));
        assertEquals(5, movies.size());
    }

    @Test
    void testAddMovie_PropagatesDatabaseException() throws DatabaseException {
        Movie movie = new Movie("Titanic", "Drama", 1997);
        doThrow(new DatabaseException("connection lost"))
                .when(movieRepository).add(movie);

        assertThrows(DatabaseException.class, () -> movieService.addMovie(movie));
    }
}
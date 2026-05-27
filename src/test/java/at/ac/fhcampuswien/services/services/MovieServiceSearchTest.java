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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// search-related tests for MovieService.
// findAll() on the mock repository returns a fixed catalog used by every test.
public class MovieServiceSearchTest {

    private MovieService movieService;
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() throws DatabaseException, MovieNotFoundException {
        movieRepository = mock(MovieRepository.class);

        List<Movie> catalog = new ArrayList<>(Arrays.asList(
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
                        .build(),
                new Movie.MovieBuilder("Titanic")
                        .genre("Romance")
                        .releaseYear(1997)
                        .build()
        ));

        when(movieRepository.findAll()).thenReturn(catalog);

        movieService = new MovieService(movieRepository);
    }

    @Test
    void givenExactTitle_whenSearchMovies_thenReturnsMatchingMovie() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("Inception", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnsMatchingMovie() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("ncep", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowercaseTitle_whenSearchMovies_thenSearchIsCaseInsensitive() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("inception", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenGenre_whenSearchMovies_thenReturnsAllMoviesWithGenre() throws DatabaseException {
        List<Movie> result = movieService.searchMovies(null, "Sci-Fi", null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(movie -> movie.getGenre().equals("Sci-Fi")));
    }

    @Test
    void givenLowercaseGenre_whenSearchMovies_thenSearchIsCaseInsensitive() throws DatabaseException {
        List<Movie> result = movieService.searchMovies(null, "sci-fi", null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(movie -> movie.getGenre().equals("Sci-Fi")));
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnsMovieFromThatYear() throws DatabaseException {
        List<Movie> result = movieService.searchMovies(null, null, "2010");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenTitleGenreAndReleaseYear_whenSearchMovies_thenReturnsOnlyMatchingMovie() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("incep", "sci-fi", "2010");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenNoMatchingSearchValues_whenSearchMovies_thenReturnsEmptyList() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("Unknown", "Drama", "2025");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptySearchValues_whenSearchMovies_thenReturnsAllMovies() throws DatabaseException {
        List<Movie> result = movieService.searchMovies("", "", "");

        assertEquals(4, result.size());
    }

    @Test
    void givenNullSearchValues_whenSearchMovies_thenReturnsAllMovies() throws DatabaseException {
        List<Movie> result = movieService.searchMovies(null, null, null);

        assertEquals(4, result.size());
    }

    @Test
    void givenDatabaseError_whenSearchMovies_propagatesDatabaseException() throws DatabaseException {
        when(movieRepository.findAll()).thenThrow(new DatabaseException("connection lost"));

        assertThrows(DatabaseException.class, () -> movieService.searchMovies("Inception", null, null));
    }
}

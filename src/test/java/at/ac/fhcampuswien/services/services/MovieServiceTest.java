
package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;
    private List<Movie> movies;

    @BeforeEach
    void setUp() {
        movies = new ArrayList<>();

        movies.add(new Movie("Inception", "Sci-Fi", 2010));
        movies.add(new Movie("The Dark Knight", "Action", 2008));
        movies.add(new Movie("Interstellar", "Sci-Fi", 2014));
        movies.add(new Movie("Titanic", "Romance", 1997));

        movieService = new MovieService(movies);
    }

    @Test
    void givenExactTitle_whenSearchMovies_thenReturnsMatchingMovie() {
        List<Movie> result = movieService.searchMovies("Inception", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnsMatchingMovie() {
        List<Movie> result = movieService.searchMovies("ncep", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowercaseTitle_whenSearchMovies_thenSearchIsCaseInsensitive() {
        List<Movie> result = movieService.searchMovies("inception", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenGenre_whenSearchMovies_thenReturnsAllMoviesWithGenre() {
        List<Movie> result = movieService.searchMovies(null, "Sci-Fi", null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(movie -> movie.getGenre().equals("Sci-Fi")));
    }

    @Test
    void givenLowercaseGenre_whenSearchMovies_thenSearchIsCaseInsensitive() {
        List<Movie> result = movieService.searchMovies(null, "sci-fi", null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(movie -> movie.getGenre().equals("Sci-Fi")));
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnsMovieFromThatYear() {
        List<Movie> result = movieService.searchMovies(null, null, "2010");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenTitleGenreAndReleaseYear_whenSearchMovies_thenReturnsOnlyMatchingMovie() {
        List<Movie> result = movieService.searchMovies("incep", "sci-fi", "2010");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenNoMatchingSearchValues_whenSearchMovies_thenReturnsEmptyList() {
        List<Movie> result = movieService.searchMovies("Unknown", "Drama", "2025");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptySearchValues_whenSearchMovies_thenReturnsAllMovies() {
        List<Movie> result = movieService.searchMovies("", "", "");

        assertEquals(4, result.size());
    }

    @Test
    void givenNullSearchValues_whenSearchMovies_thenReturnsAllMovies() {
        List<Movie> result = movieService.searchMovies(null, null, null);

        assertEquals(4, result.size());
    }
}
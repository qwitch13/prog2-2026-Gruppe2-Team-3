package at.ac.fhcampuswien.services.services;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class MovieServiceAddTest {
    private MovieService movieService;
    private List<Movie> testMovies;
    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @BeforeEach
    void setUp() {
        testMovies = new ArrayList<>();

        movie1 = new Movie("Inception", "Sci-Fi", 2010);
        movie2 = new Movie("The Dark Knight", "Action", 2008);
        movie3 = new Movie("Interstellar", "Sci-Fi", 2014);

        testMovies.add(movie1);
        testMovies.add(movie2);
        testMovies.add(movie3);

        movieService = new MovieService(testMovies);
    }

    @Test
    void testAddMovie_Success() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result, "Should return true when movie is added");
        assertEquals(4, testMovies.size(), "Movie list should contain one more movie");
        assertTrue(testMovies.contains(newMovie), "Movie list should contain the added movie");
    }

    @Test
    void testAddMovie_AddsCorrectTitle() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        assertEquals("Titanic", testMovies.get(3).getTitle());
    }

    @Test
    void testAddMovie_AddsCorrectGenre() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        assertEquals("Drama", testMovies.get(3).getGenre());
    }

    @Test
    void testAddMovie_AddsCorrectReleaseYear() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        assertEquals(1997, testMovies.get(3).getReleaseYear());
    }

    @Test
    void testAddMovie_DoesNotModifyExistingMovies() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        assertEquals("Inception", movie1.getTitle());
        assertEquals("The Dark Knight", movie2.getTitle());
        assertEquals("Interstellar", movie3.getTitle());
    }

    @Test
    void testAddMovie_MultipleMovies() {
        Movie movie4 = new Movie("Titanic", "Drama", 1997);
        Movie movie5 = new Movie("Avatar", "Sci-Fi", 2009);

        movieService.addMovie(movie4);
        movieService.addMovie(movie5);

        assertEquals(5, testMovies.size());
        assertTrue(testMovies.contains(movie4));
        assertTrue(testMovies.contains(movie5));
    }

    @Test
    void testAddMovie_EmptyTitle() {
        Movie newMovie = new Movie("", "Drama", 1997);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result, "Should allow empty title");
        assertEquals("", testMovies.get(3).getTitle());
    }

    @Test
    void testAddMovie_ZeroReleaseYear() {
        Movie newMovie = new Movie("Unknown Movie", "Drama", 0);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result, "Should allow zero release year");
        assertEquals(0, testMovies.get(3).getReleaseYear());
    }

    @Test
    void testAddMovie_PreservesGeneratedId() {
        Movie newMovie = new Movie("Titanic", "Drama", 1997);

        movieService.addMovie(newMovie);

        assertNotNull(newMovie.getId(), "Added movie should have an ID");
        assertEquals(newMovie.getId(), testMovies.get(3).getId(), "ID should remain the same");
    }
}

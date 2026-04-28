package at.ac.fhcampuswien.services.services;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceDeleteTest {

    private MovieService movieService;
    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        testMovies = new ArrayList<>();
        Movie movie1 = new Movie("Inception", "Sci-Fi", 2010);
        Movie movie2 = new Movie("The Dark Knight", "Action", 2008);
        Movie movie3 = new Movie("Interstellar", "Sci-Fi", 2014);

        testMovies.add(movie1);
        testMovies.add(movie2);
        testMovies.add(movie3);

        movieService = new MovieService(testMovies);
    }

@Test
    void givenCorrectMovie_whenDelete_deletesMovie(){
    Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);

    boolean result = movieService.deleteMovie(movieToDelete);

    assertTrue(result, "The movie should be successfully deleted");
    assertFalse(testMovies.contains(movieToDelete), "The list should no longer contain the deleted movie");
    }

    @Test
    void givenNonExistingMovie_whenDelete_returnsFalse() {
        Movie nonExistentMovie = new Movie("Avatar", "Sci-Fi", 2009);

        boolean result = movieService.deleteMovie(nonExistentMovie);

        assertFalse(result, "The service should return false when the movie is not found");
    }

    @Test
    void givenEmptyJsonObject_whenDelete_returnsFalse() {
        Movie emptyMovie = new Movie(null, null, 0);

        boolean result = movieService.deleteMovie(emptyMovie);

        assertFalse(result, "An empty movie object should not be deletable");
    }

    @Test
    void givenNullMovie_whenDelete_returnsFalse() {
        boolean result = movieService.deleteMovie(null);

        assertFalse(result, "The service should handle null inputs without crashing");
    }

    @Test
    void givenIncompleteMovieDataNoTitle_whenDelete_returnsFalse() {
        Movie incompleteMovie = new Movie(null, "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(incompleteMovie);

        assertFalse(result, "Should return false if the movie data is incomplete or invalid");
    }

    @Test
    void givenIncompleteMovieDataNoGenre_whenDelete_returnsFalse() {
        Movie incompleteMovie = new Movie("Inception", null, 2010);

        boolean result = movieService.deleteMovie(incompleteMovie);

        assertFalse(result, "Should return false if the movie data is incomplete or invalid");
    }

    @Test
    void givenIncompleteMovieDataNoYear_whenDelete_returnsFalse() {
        Movie incompleteMovie = new Movie("Inception", "Sci-Fi", 0);

        boolean result = movieService.deleteMovie(incompleteMovie);

        assertFalse(result, "Should return false if the movie data is incomplete or invalid");
    }

    @Test
    void givenCorrectTitleButWrongYear_whenDelete_returnsFalse() {
        Movie wrongYearMovie = new Movie("Inception", "Sci-Fi", 2024);

        boolean result = movieService.deleteMovie(wrongYearMovie);

        assertFalse(result, "Should only delete if all fields (Title, Genre, Year) match exactly");
    }

    @Test
    void givenCorrectTitleButWrongGenre_whenDelete_returnsFalse() {
        Movie wrongYearMovie = new Movie("Inception", "Action", 2010);

        boolean result = movieService.deleteMovie(wrongYearMovie);

        assertFalse(result, "Should only delete if all fields (Title, Genre, Year) match exactly");
    }

    @Test
    void givenWrongCasing_whenDelete_returnsFalse() {
        Movie lowercaseMovie = new Movie("inception", "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(lowercaseMovie);

        assertFalse(result, "Deletion should fail if the casing does not match exactly");
    }

}

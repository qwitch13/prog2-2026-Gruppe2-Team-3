package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieServiceUpdateTest {
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
    void testUpdateMovie_SuccessUpdateTitle() {
        Movie updatedMovie = new Movie("Inception Reborn", "Sci-Fi", 2010);
        updatedMovie.setId(movie1.getId());

        boolean result = movieService.updateMovie(updatedMovie);
        assertTrue(result, "Should return true when update successful");
        assertEquals("Inception Reborn", movie1.getTitle(), "Should update title");
        assertEquals("Sci-Fi", movie1.getGenre(), "Genre should remain unchanged");
    }

    @Test
    void testUpdateMovie_SuccessUpdateGenre() {
        Movie updatedMovie = new Movie("Inception", "Drama", 2010);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals("Drama", movie1.getGenre(), "Should update genre");
        assertEquals("Inception", movie1.getTitle(), "Title should remain unchanged");
    }

    @Test
    void testUpdateMovie_SuccessUpdateReleaseYear() {
        Movie updatedMovie = new Movie("Inception", "Sci-Fi", 2011);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals(2011, movie1.getReleaseYear(), "Should update release year");
    }

    @Test
    void testUpdateMovie_UpdateAllFields() {
        Movie updatedMovie = new Movie("Inception Extended", "Thriller", 2015);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals("Inception Extended", movie1.getTitle());
        assertEquals("Thriller", movie1.getGenre());
        assertEquals(2015, movie1.getReleaseYear());
    }

    @Test
    void testUpdateMovie_NotFound() {
        Movie nonexistent = new Movie("Titanic", "Drama", 1997);
        boolean result = movieService.updateMovie(nonexistent);
        assertFalse(result, "Should return false when movie not found");
    }

    @Test
    void testUpdateMovie_WrongId_NoChange() {
        UUID wrongId = UUID.randomUUID();
        Movie updatedMovie = new Movie("New Title", "New Genre", 1990);
        updatedMovie.setId(wrongId);

        boolean result = movieService.updateMovie(updatedMovie);
        assertFalse(result, "Should return false with non-matching ID");
        assertEquals("Inception", movie1.getTitle(), "Original movie should not change");
    }

    @Test
    void testUpdateMovie_PreservesId() {
        UUID originalId = movie1.getId();
        Movie updatedMovie = new Movie("New Title", "Sci-Fi", 2010);
        updatedMovie.setId(originalId);

        movieService.updateMovie(updatedMovie);
        assertEquals(originalId, movie1.getId(), "ID should remain unchanged");
    }

    @Test
    void testUpdateMovie_UpdateDoesNotAffectOtherMovies() {
        Movie updatedMovie = new Movie("New Title", "New Genre", 2020);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals("The Dark Knight", movie2.getTitle(), "Other movies should not change");
        assertEquals("Interstellar", movie3.getTitle());
    }

    @Test
    void testUpdateMovie_MultipleUpdates() {
        Movie updatedMovie1 = new Movie("Inception v2", "Sci-Fi", 2012);
        updatedMovie1.setId(movie1.getId());
        movieService.updateMovie(updatedMovie1);

        Movie updatedMovie2 = new Movie("Inception v3", "Action", 2013);
        updatedMovie2.setId(movie1.getId());
        movieService.updateMovie(updatedMovie2);

        assertEquals("Inception v3", movie1.getTitle());
        assertEquals("Action", movie1.getGenre());
        assertEquals(2013, movie1.getReleaseYear());
    }

    @Test
    void testUpdateMovie_EmptyStringTitle() {
        Movie updatedMovie = new Movie("", "Sci-Fi", 2010);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals("", movie1.getTitle(), "Should allow empty title");
    }

    @Test
    void testUpdateMovie_ZeroReleaseYear() {
        Movie updatedMovie = new Movie("Inception", "Sci-Fi", 0);
        updatedMovie.setId(movie1.getId());

        movieService.updateMovie(updatedMovie);
        assertEquals(0, movie1.getReleaseYear(), "Should allow zero release year");
    }
}
package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MovieServiceTest {
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        // Punkt 50: Initialisierung der Testdaten
        List<Movie> testMovies = new ArrayList<>();
        testMovies.add(new Movie("Inception", "Sci-Fi", 2010));
        testMovies.add(new Movie("The Dark Sword", "Thriller", 1942));

        // Punkt 30 & 55: Constructor Injection (macht das Testen einfacher!)
        movieService = new MovieService(testMovies);
    }

    /**
     * Test für searchMovies (Punkt 48).
     * Prüft Case-Insensitivity (Punkt 12) und Teilstrings (Punkt 13).
     */
    @Test
    void searchMovies_ByPartialTitleIgnoreCase_ReturnsFilteredList() {
        // Arrange (Given)
        String query = "incep";

        // Act (When)
        List<Movie> result = movieService.searchMovies(query, "", "");

        // Assert (Then)
        assertNotNull(result);
        assertEquals(1, result.size(), "Es sollte genau ein Film gefunden werden");
        assertEquals("Inception", result.get(0).getTitle());
    }
}
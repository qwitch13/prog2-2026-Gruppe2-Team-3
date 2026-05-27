package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// SOLID - Single Responsibility Principle
// Separation of concerns
public class GenerateMovieList {
    public static List<Movie> generateDummy() { // Generate a list of dummy movies
        List<Movie> movies = new ArrayList<>();

        String[] genres = {
                "Action", "Drama", "Comedy", "Horror", "Sci-Fi",
                "Thriller", "Fantasy", "Romance", "Adventure", "Mystery"
        };

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie.MovieBuilder(DummyGenerator.generateMovieTitle())   // Use random title
                    .genre(genres[random.nextInt(genres.length)])   // Random genre
                    .releaseYear(1900 + random.nextInt(126))   // Random release year between 1900 and 2026
                    .build();
            movies.add(movie);
        }
        return movies;
    }
}

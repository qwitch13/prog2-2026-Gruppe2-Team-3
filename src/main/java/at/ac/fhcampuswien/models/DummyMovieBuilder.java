package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds dummy movie data for database initialization.
 *
 * This class has one responsibility:
 * creating a list of dummy movies.
 */
public class DummyMovieBuilder {

    private DummyMovieBuilder() {
    }

    public static List<Movie> buildDummyMovies() {
        List<Movie> movies = new ArrayList<>();

        String[] genres = {
                "Action", "Drama", "Comedy", "Horror", "Sci-Fi",
                "Thriller", "Fantasy", "Romance", "Adventure", "Mystery"
        };

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie.MovieBuilder()
                    .title(DummyGenerator.generateMovieTitle())
                    .genre(genres[random.nextInt(genres.length)])
                    .releaseYear(1900 + random.nextInt(126))
                    .build();

            movies.add(movie);
        }

        return movies;
    }
}

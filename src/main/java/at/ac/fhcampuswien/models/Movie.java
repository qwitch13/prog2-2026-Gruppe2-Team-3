package at.ac.fhcampuswien.models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList; // Import the ArrayList class for dynamic arrays
import java.util.List; // Import the List interface for generic collections
import java.util.Random; // Import the Random class for generating random numbers
import java.util.UUID; // Import the UUID class for generating unique identifiers

public class Movie {
    private UUID id; // Unique identifier for each movie
    private String title; // Title of the movie
    private String genre; // Genre of the movie
    private int releaseYear; // Year of release

    public Movie() {
        this.id = UUID.randomUUID();
    } // Default constructor

    public Movie(String title, String genre, int releaseYear) { // Constructor with parameters
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    // Getters and setters for movie properties
    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override // Override the toString method to provide a more readable representation of the Movie object
    public String toString() {
        return "Movie{" + "id=<" + id + ">, title='<" + title + ">', " + ", genre='<" + genre + ">', " + ", releaseYear=<" + releaseYear + ">}";
    }

    public static List<Movie> generateDummyMovies() { // Generate a list of dummy movies
        List<Movie> movies = new ArrayList<>();

        String[] genres = {
                "Action", "Drama", "Comedy", "Horror", "Sci-Fi",
                "Thriller", "Fantasy", "Romance", "Adventure", "Mystery"
        };

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            // builder pattern (creational): fluent and self-documenting object
            // construction; the resulting Movie still has its UUID assigned by
            // the no-arg constructor, so callers never deal with null ids.
            movies.add(Movie.builder()
                    .title(DummyGenerator.generateMovieTitle())
                    .genre(genres[random.nextInt(genres.length)])
                    .releaseYear(1900 + random.nextInt(126))
                    .build());
        }

        return movies;
    }

    // entry point for the builder pattern.
    public static Builder builder() {
        return new Builder();
    }

    // fluent builder for Movie.
    // chosen over a many-arg constructor because:
    //   - field meaning is visible at the call site (title("...") vs. positional args),
    //   - it allows partial construction in tests without overloading the constructor,
    //   - adding new optional attributes does not break existing callers.
    public static final class Builder {
        private UUID id;
        private String title;
        private String genre;
        private int releaseYear;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder releaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Movie build() {
            Movie movie = new Movie();
            if (id != null) {
                movie.setId(id);
            }
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setReleaseYear(releaseYear);
            return movie;
        }
    }
}
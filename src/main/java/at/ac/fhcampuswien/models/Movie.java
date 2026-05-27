package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Movie {
    private UUID id; // Unique identifier for each movie
    private String title; // Title of the movie
    private String genre; // Genre of the movie
    private int releaseYear; // Year of release

    // Default constructor needed for Gson and general object creation
    public Movie() {
        this.id = UUID.randomUUID();
    }

    // Existing constructor kept so old code and tests do not break
    public Movie(String title, String genre, int releaseYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    // Private constructor used by the nested MovieBuilder
    private Movie(MovieBuilder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.title = builder.title;
        this.genre = builder.genre;
        this.releaseYear = builder.releaseYear;
    }

    /**
     * Builder Pattern.
     *
     * This nested static class creates Movie objects step by step.
     * Each method sets one property and returns the builder itself.
     * The build() method creates the final Movie object.
     */
    public static class MovieBuilder {
        private UUID id;
        private String title;
        private String genre;
        private int releaseYear;

        public MovieBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public MovieBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MovieBuilder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public MovieBuilder releaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
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

    // Provides a readable representation of the Movie object
    @Override
    public String toString() {
        return "Movie{" +
                "id=<" + id + ">, " +
                "title='<" + title + ">', " +
                "genre='<" + genre + ">', " +
                "releaseYear=<" + releaseYear + ">" +
                "}";
    }


}
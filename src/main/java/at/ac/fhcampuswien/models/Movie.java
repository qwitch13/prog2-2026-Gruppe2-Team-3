package at.ac.fhcampuswien.models;

import java.util.ArrayList; // Import the ArrayList class for dynamic arrays
import java.util.List; // Import the List interface for generic collections
import java.util.Random; // Import the Random class for generating random numbers
import java.util.UUID; // Import the UUID class for generating unique identifiers

public class Movie {

    private UUID id; // Unique identifier for each movie
    private String title; // Title of the movie
    private String genre; // Genre of the movie
    private int releaseYear; // Year of release

    // deleted all constructors that we now don't need with Builder
    public Movie(MovieBuilder movieBuilder) { // Constructor with parameters
        this.id = movieBuilder.id;
        this.title = movieBuilder.title;
        this.genre = movieBuilder.genre;
        this.releaseYear = movieBuilder.releaseYear;
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

    // created Builder
    public static class MovieBuilder {
        private UUID id;
        private String title;
        private String genre;
        private int releaseYear;

        public MovieBuilder(String title) {
            this.title = title;
        }

        public MovieBuilder id() {
            this.id = UUID.randomUUID();
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
}
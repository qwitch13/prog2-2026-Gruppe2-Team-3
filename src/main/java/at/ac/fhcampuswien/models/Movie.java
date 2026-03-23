package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Movie {

    private UUID id;
    private String title;
    private String genre;
    private int releaseYear;

    public Movie() {
        this.id = UUID.randomUUID();
    }

    public Movie(String title, String genre, int releaseYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public static List<Movie> generateDummyMovies() {
        List<Movie> movies = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            movies.add(new Movie("Movie " + i, "Genre " + i, 2000 + i));
        }
        return movies;
    }
    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getReleaseYear() { return releaseYear; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }


    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"genre\":\"" + genre + "\"," +
                "\"releaseYear\":" + releaseYear +
                "}";
    }

}



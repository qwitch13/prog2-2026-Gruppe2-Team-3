package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MovieController implements HttpHandler {
    private static final String BASE = "/api/movies/";

    private final MovieService movieService;
    private final Gson gson = new Gson();

    public MovieController() {
        this(new MovieService(new MovieRepository()));
    }

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (path) {
                case BASE + "getAll" -> handleGetAll(method, exchange);
                case BASE + "add" -> handleAdd(method, exchange);
                case BASE + "search" -> handleSearchMovies(exchange);
                case BASE + "delete" -> handleDelete(method, exchange);
                case BASE + "update" -> handleUpdate(method, exchange);
                default -> ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
            }
        } catch (SQLException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"Database error\" }");
        }
    }

    private void handleGetAll(String method, HttpExchange exchange) throws IOException, SQLException {
        if (!"GET".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String response = gson.toJson(movieService.getAllMovies());
        ApiUtils.sendResponse(exchange, 200, response);
    }

    private void handleSearchMovies(HttpExchange exchange) throws IOException, SQLException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ApiUtils.parseQueryParams(query);

        String title = params.getOrDefault("title", "");
        String genre = params.getOrDefault("genre", "");
        String releaseYear = params.getOrDefault("releaseYear", "");

        List<Movie> filteredMovies = movieService.searchMovies(title, genre, releaseYear);
        ApiUtils.sendResponse(exchange, 200, gson.toJson(filteredMovies));
    }

    private void handleAdd(String method, HttpExchange exchange) throws IOException, SQLException {
        if (!"POST".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie movie = gson.fromJson(requestBody, Movie.class);

        if (movie == null || movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() == 0) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        if (movieService.movieExists(movie)) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
            return;
        }

        movieService.addMovie(movie);
        ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
    }

    private void handleDelete(String method, HttpExchange exchange) throws IOException, SQLException {
        if (!"DELETE".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie movie = gson.fromJson(requestBody, Movie.class);

        if (movie == null || movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() == 0) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        boolean deleted = movieService.deleteMovie(movie);
        if (deleted) {
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
        } else {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        }
    }

    private void handleUpdate(String method, HttpExchange exchange) throws IOException, SQLException {
        if (!"PUT".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

        if (updatedMovie == null || updatedMovie.getId() == null || updatedMovie.getTitle() == null
                || updatedMovie.getGenre() == null || updatedMovie.getReleaseYear() == 0) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        boolean updated = movieService.updateMovie(updatedMovie);
        if (updated) {
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
        } else {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        }
    }
}

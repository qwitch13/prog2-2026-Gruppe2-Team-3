package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// controller for the /api/movies/ resource.
// every request is dispatched first by exact path, then by http method.
// each handler is wrapped in a try/catch block that maps the application
// exceptions to the expected http status codes:
//   MovieNotFoundException -> 404
//   DatabaseException      -> 500
//   JsonSyntaxException    -> 400
//   any other Throwable    -> 500 (safety net so the server never leaks stack traces)
public class MovieController implements HttpHandler {
    private static final String BASE = "/api/movies/";

    private final MovieService movieService;
    private final Gson gson = new Gson();

    // default constructor used in Main: wires service to the real repository.
    public MovieController() {
        this(new MovieService(new MovieRepository()));
    }

    // testing/main-demo constructor that accepts a pre-built service.
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleGetAll(method, exchange);
            case BASE + "add" -> handleAdd(method, exchange);
            case BASE + "search" -> handleSearchMovies(exchange);
            case BASE + "delete" -> handleDelete(method, exchange);
            case BASE + "update" -> handleUpdate(method, exchange);
            default -> ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }

    // GET /api/movies/getAll - returns all movies.
    private void handleGetAll(String method, HttpExchange exchange) throws IOException {
        if (!"GET".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String response = gson.toJson(movieService.getAllMovies());
            ApiUtils.sendResponse(exchange, 200, response);
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Database error: " + e.getMessage()));
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Internal server error"));
        }
    }

    // GET /api/movies/search?title=...&genre=...&releaseYear=...
    private void handleSearchMovies(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        try {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = ApiUtils.parseQueryParams(query);

            String title = params.getOrDefault("title", "");
            String genre = params.getOrDefault("genre", "");
            String releaseYear = params.getOrDefault("releaseYear", "");

            List<Movie> filteredMovies = movieService.searchMovies(title, genre, releaseYear);
            ApiUtils.sendResponse(exchange, 200, gson.toJson(filteredMovies));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Database error: " + e.getMessage()));
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Internal server error"));
        }
    }

    // POST /api/movies/add - body: { "title": ..., "genre": ..., "releaseYear": ... }
    private void handleAdd(String method, HttpExchange exchange) throws IOException {
        if (!"POST".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (movie == null
                    || movie.getTitle() == null
                    || movie.getGenre() == null
                    || movie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, errorJson("Invalid movie data"));
                return;
            }

            if (movieService.movieExists(movie)) {
                ApiUtils.sendResponse(exchange, 400, errorJson("Movie already exists"));
                return;
            }

            movieService.addMovie(movie);
            ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, errorJson("Malformed JSON: " + e.getMessage()));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Database error: " + e.getMessage()));
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Internal server error"));
        }
    }

    // DELETE /api/movies/delete - body identifies the movie by title/genre/releaseYear.
    private void handleDelete(String method, HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (movie == null
                    || movie.getTitle() == null
                    || movie.getGenre() == null
                    || movie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, errorJson("Invalid movie data"));
                return;
            }

            movieService.deleteMovie(movie);
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, errorJson("Malformed JSON: " + e.getMessage()));
        } catch (MovieNotFoundException e) {
            ApiUtils.sendResponse(exchange, 404, errorJson(e.getMessage()));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Database error: " + e.getMessage()));
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Internal server error"));
        }
    }

    // PUT /api/movies/update - body must include id plus updated title/genre/releaseYear.
    private void handleUpdate(String method, HttpExchange exchange) throws IOException {
        if (!"PUT".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

            if (updatedMovie == null
                    || updatedMovie.getId() == null
                    || updatedMovie.getTitle() == null
                    || updatedMovie.getGenre() == null
                    || updatedMovie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, errorJson("Invalid movie data"));
                return;
            }

            movieService.updateMovie(updatedMovie);
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, errorJson("Malformed JSON: " + e.getMessage()));
        } catch (MovieNotFoundException e) {
            ApiUtils.sendResponse(exchange, 404, errorJson(e.getMessage()));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Database error: " + e.getMessage()));
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, errorJson("Internal server error"));
        }
    }

    // small helper that wraps a plain message in the canonical error envelope.
    private String errorJson(String message) {
        return "{ \"error\": \"" + message.replace("\"", "\\\"") + "\" }";
    }
}

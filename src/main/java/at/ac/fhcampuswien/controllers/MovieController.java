package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"Database error\" }");
        }
    }

    private void handleGetAll(String method, HttpExchange exchange) throws IOException {
        if (!"GET".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String response = gson.toJson(movieService.getAllMovies());
        ApiUtils.sendResponse(exchange, 200, response);
    }

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
        }  catch (DatabaseException e){
            ApiUtils.sendResponse(exchange, 500, "{ \"ERROR\": \"Internal Server Error\"}" + e);
        }

    }

    private void handleAdd(String method, HttpExchange exchange) throws IOException {
        if (!"POST".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
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
        } catch (DatabaseException e){
            ApiUtils.sendResponse(exchange, 500, "{ \"ERROR\": \"Internal Server Error\"}" + e);
        } catch (MalformedJsonException e){
            ApiUtils.sendResponse(exchange, 400, "{ \"ERROR\": \"Malformed JSON\" }" + e);
        }
    }

    private void handleDelete(String method, HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (movie == null || movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            movieService.deleteMovie(movie);
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");

        } catch (MovieNotFoundException e){
            ApiUtils.sendResponse(exchange, 404, "{ \"ERROR\": \"Movie not found\" }" + e);
        } catch (MalformedJsonException e){
            ApiUtils.sendResponse(exchange, 400, "{ \"ERROR\": \"Malformed JSON\" }" + e);
        }catch (DatabaseException e){
            ApiUtils.sendResponse(exchange, 500, "{ \"ERROR\": \"Internal Server Error\"}" + e);
        }

    }

    private void handleUpdate(String method, HttpExchange exchange) throws IOException {
        if (!"PUT".equals(method)) {
            ApiUtils.sendResponse(exchange, 405, "{ \"ERROR\": \"Method not allowed\" }");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Movie updatedMovie = gson.fromJson(requestBody, Movie.class);
            if (updatedMovie == null || updatedMovie.getId() == null || updatedMovie.getTitle() == null
                    || updatedMovie.getGenre() == null || updatedMovie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, "{ \"ERROR\": \"Invalid movie data\" }");
                return;
            }
            movieService.updateMovie(updatedMovie);
            ApiUtils.sendResponse(exchange, 200, "{ \"MESSAGE\": \"Movie updated successfully\" }");

        } catch (MovieNotFoundException e){
            ApiUtils.sendResponse(exchange, 404, "{ \"ERROR\": \"Movie not found\" }" + e);
        } catch (MalformedJsonException e){
            ApiUtils.sendResponse(exchange, 400, "{ \"ERROR\": \"Malformed JSON\" }" + e);
        } catch (DatabaseException e){
            ApiUtils.sendResponse(exchange, 500, "{ \"ERROR\": \"Internal Server Error\"}" + e);
        }

    }
}

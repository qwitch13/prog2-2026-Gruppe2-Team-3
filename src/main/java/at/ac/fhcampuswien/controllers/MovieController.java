package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.routing.RouteRegistry;
import at.ac.fhcampuswien.exceptions.HttpExceptionMapper;
import at.ac.fhcampuswien.exceptions.HttpExceptionMapper.HttpError;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.JdbcMovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// HTTP-facing layer for /api/movies/. after the exercise-4 refactor this class
// is responsible for one thing only: turning HttpExchanges into MovieService
// calls and writing the response. it delegates:
//   - path/method dispatch to RouteRegistry  (OCP - new routes don't touch handle())
//   - exception -> http translation to HttpExceptionMapper  (SRP, behavioural pattern)
//   - persistence to MovieRepository through MovieService  (DIP)
public class MovieController implements HttpHandler {
    private static final String BASE = "/api/movies/";

    private final MovieService movieService;
    private final Gson gson = new Gson();
    private final RouteRegistry routes = new RouteRegistry();
    private final HttpExceptionMapper exceptionMapper = HttpExceptionMapper.defaultMapper();

    // default constructor used in Main: wires service to the real repository.
    public MovieController() {
        this(new MovieService(new JdbcMovieRepository()));
    }

    // testing/main-demo constructor that accepts a pre-built service.
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
        registerRoutes();
    }

    // route table - the only place that grows when a new endpoint is added.
    private void registerRoutes() {
        routes.register("GET", BASE + "getAll", this::handleGetAll);
        routes.register("GET", BASE + "search", this::handleSearchMovies);
        routes.register("POST", BASE + "add", this::handleAdd);
        routes.register("DELETE", BASE + "delete", this::handleDelete);
        routes.register("PUT", BASE + "update", this::handleUpdate);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        routes.dispatch(exchange);
    }

    // GET /api/movies/getAll - returns all movies.
    private void handleGetAll(HttpExchange exchange) throws IOException {
        runHandled(exchange, () -> {
            String response = gson.toJson(movieService.getAllMovies());
            ApiUtils.sendResponse(exchange, 200, response);
        });
    }

    // GET /api/movies/search?title=...&genre=...&releaseYear=...
    private void handleSearchMovies(HttpExchange exchange) throws IOException {
        runHandled(exchange, () -> {
            Map<String, String> params = ApiUtils.parseQueryParams(exchange.getRequestURI().getQuery());

            String title = params.getOrDefault("title", "");
            String genre = params.getOrDefault("genre", "");
            String releaseYear = params.getOrDefault("releaseYear", "");

            List<Movie> filteredMovies = movieService.searchMovies(title, genre, releaseYear);
            ApiUtils.sendResponse(exchange, 200, gson.toJson(filteredMovies));
        });
    }

    // POST /api/movies/add
    private void handleAdd(HttpExchange exchange) throws IOException {
        runHandled(exchange, () -> {
            String requestBody = readBody(exchange);
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (movie == null
                    || movie.getTitle() == null
                    || movie.getGenre() == null
                    || movie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, HttpExceptionMapper.errorJson("Invalid movie data"));
                return;
            }

            if (movieService.movieExists(movie)) {
                ApiUtils.sendResponse(exchange, 400, HttpExceptionMapper.errorJson("Movie already exists"));
                return;
            }

            movieService.addMovie(movie);
            ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
        });
    }

    // DELETE /api/movies/delete
    private void handleDelete(HttpExchange exchange) throws IOException {
        runHandled(exchange, () -> {
            String requestBody = readBody(exchange);
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (movie == null
                    || movie.getTitle() == null
                    || movie.getGenre() == null
                    || movie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, HttpExceptionMapper.errorJson("Invalid movie data"));
                return;
            }

            movieService.deleteMovie(movie);
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
        });
    }

    // PUT /api/movies/update
    private void handleUpdate(HttpExchange exchange) throws IOException {
        runHandled(exchange, () -> {
            String requestBody = readBody(exchange);
            Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

            if (updatedMovie == null
                    || updatedMovie.getId() == null
                    || updatedMovie.getTitle() == null
                    || updatedMovie.getGenre() == null
                    || updatedMovie.getReleaseYear() == 0) {
                ApiUtils.sendResponse(exchange, 400, HttpExceptionMapper.errorJson("Invalid movie data"));
                return;
            }

            movieService.updateMovie(updatedMovie);
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
        });
    }

    // runs a handler body and routes any thrown exception through the central mapper.
    // keeps every handler free of its own try/catch tower (SRP).
    private void runHandled(HttpExchange exchange, ThrowingRunnable action) throws IOException {
        try {
            action.run();
        } catch (IOException e) {
            // bubble up i/o errors so the http server can react properly
            throw e;
        } catch (Exception e) {
            HttpError error = exceptionMapper.map(e);
            ApiUtils.sendResponse(exchange, error.status(), error.body());
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    // local checked-exception aware Runnable so handlers can throw freely.
    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}

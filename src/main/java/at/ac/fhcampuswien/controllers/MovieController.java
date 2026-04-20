package at.ac.fhcampuswien.controllers; //Controller for managing movies - handles CRUD operations for Movie entities

import at.ac.fhcampuswien.ApiUtils; //Helper class for sending HTTP responses
import at.ac.fhcampuswien.models.Movie; //Movie model
import com.sun.net.httpserver.HttpExchange; //HTTP exchange object
import com.sun.net.httpserver.HttpHandler; //HTTP handler interface
import at.ac.fhcampuswien.services.MovieService; //Service layer for business logic
import java.io.IOException; //Exception class for I/O errors
import java.nio.charset.StandardCharsets; //Charset for string encoding
import java.util.List; //List interface for generic collections
import java.util.UUID; //UUID class for unique identifiers

/**──────────────────────────────────────────────
 * Controller for managing movies - handles CRUD operations for Movie entities
 * ──────────────────────────────────────────────**/

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies/"; //Base path for movie endpoints
    /** Changed for EX2  **/
    private final MovieService movieService; //Service layer for business logic

    /**
     * constructor for MovieController with dependency injection of MovieService.
     * Initializes the service with dummy movie data.
     */

    public MovieController() {
        List<Movie> movies = Movie.generateDummyMovies(); //List of dummy movies
        this.movieService = new MovieService(movies); //Inject the movie list into the service
    }
    /** END CHANGE for EX2 **/

    @Override //Handle HTTP requests for movie-related endpoints
    public void handle(HttpExchange exchange) throws IOException { //Handle the HTTP request and send the response
        String method = exchange.getRequestMethod(); //Get the HTTP method (GET, POST, etc.)
        String path = exchange.getRequestURI().getPath(); //Get the requested URI path

        switch (path) { //Route based on the path
            case BASE + "getAll" -> handleGetAll(method, exchange); //Handle GET /api/movies/getAll
            case BASE + "add" -> handleAdd(method, exchange); //Handle POST /api/movies/add
            case BASE + "delete" -> handleDelete(method, exchange); //Handle DELETE /api/movies/delete
            case BASE + "update" -> handleUpdate(method, exchange); //Handle PUT /api/movies/update
            default -> {
                String response = "{ \"error\": \"Path not found\" }"; //Path not found
                ApiUtils.sendResponse(exchange, 404, response); //Send 404 Not Found
            }
        }
    }

    /**──────────────────────────────────────────────
     * GET /api/movies/getAll - returns a list of all movies
    ──────────────────────────────────────────────**/
    private void handleGetAll(String method, HttpExchange exchange) throws IOException { //Handle GET /api/movies/getAll request
        switch (method) { //Check if the request method is GET or not allowed: otherwise send 405 Method Not Allowed
            case "GET" -> {
                /** Changed for EX2  **/
                String response = moviesToJson(movieService.getAllMovies());
                /** END CHANGE for EX2 **/
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    /**──────────────────────────────────────────────
     * POST /api/movies/add - expects a JSON body with movie details (title, genre, releaseYear)
    ──────────────────────────────────────────────**/
    private void handleAdd(String method, HttpExchange exchange) throws IOException { //Handle POST /api/movies/add request
        switch (method) { //Check if the request method is POST or not allowed: otherwise send 405 Method Not Allowed
            case "POST" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                // Try to parse the movie from the request body
                Movie movie;
                try {
                    movie = parseMovieFromJson(requestBody);
                } catch (Exception e) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                // Validate that all required fields are present
                if (movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() == 0) { //Check if title, genre, and releaseYear are not null
                    String response = "{ \"error\": \"Invalid movie data\" }"; //Send 400 Bad Request
                    ApiUtils.sendResponse(exchange, 400, response); //Send the error response
                    return;
                }

                // Check if movie already exists using service
                /** Changed for EX2  **/
                if (movieService.movieExists(movie)) { //Use service to check if movie exists
                    String response = "{ \"error\": \"Movie already exists\" }"; //Send 400 Bad Request
                    ApiUtils.sendResponse(exchange, 400, response); //Send the error response
                    return;
                }

                movieService.addMovie(movie); //Use service to add the movie
                /** END CHANGE for EX2 **/

                String response = "{ \"message\": \"Movie added successfully\" }"; //Send 201 Created
                ApiUtils.sendResponse(exchange, 201, response); //Send the success response
            }
            default -> {//Otherwise, send 405 Method Not Allowed
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    /**──────────────────────────────────────────────
     * DELETE /api/movies/delete - expects a JSON body with the movie's title, genre, and releaseYear
     ──────────────────────────────────────────────**/

    private void handleDelete(String method, HttpExchange exchange) throws IOException { //Handle DELETE /api/movies/delete request
        switch (method) { //Check if the request method is DELETE or not allowed: otherwise send 405 Method Not Allowed
            case "DELETE" -> { //If the request method is DELETE, handle the request
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); //Get the request body

                Movie movie; //Movie object to be deleted
                try { //Try to parse the movie from the request body
                    movie = parseMovieFromJson(requestBody);
                } catch (Exception e) { //If parsing fails, send 400 Bad Request
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                if (movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() == 0) { //If title, genre, or releaseYear is null, send 400 Bad Request
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                // Use service to delete the movie
                /** Changed for EX2  **/
                boolean removed = movieService.deleteMovie(movie); //Use service to delete the movie
                /** END CHANGE for EX2 **/

                if (removed) { //If the movie was removed, send 200 OK
                    String response = "{ \"message\": \"Movie deleted successfully\" }";
                    ApiUtils.sendResponse(exchange, 200, response);
                } else { //If the movie was not found, send 404 Not Found
                    String response = "{ \"error\": \"Movie not found\" }";
                    ApiUtils.sendResponse(exchange, 404, response);
                }
            }
            default -> { //Otherwise, send 405 Method Not Allowed
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    /**──────────────────────────────────────────────
     * PUT /api/movies/update - expects a JSON body with the movie's id and updated fields (title, genre, releaseYear)
    ──────────────────────────────────────────────**/
    private void handleUpdate(String method, HttpExchange exchange) throws IOException { //Handle PUT /api/movies/update request
        switch (method) {
            case "PUT" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                // Parse the updated movie data (must include id)
                Movie updatedMovie;
                try {
                    updatedMovie = parseMovieFromJson(requestBody);
                } catch (Exception e) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                if (updatedMovie.getId() == null || updatedMovie.getTitle() == null
                        || updatedMovie.getGenre() == null || updatedMovie.getReleaseYear() == 0) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                /** Changed for EX2  **/
                if (movieService.updateMovie(updatedMovie)) { //Use service to update the movie
                    String response = "{ \"message\": \"Movie updated successfully\" }";
                    ApiUtils.sendResponse(exchange, 200, response);
                } else { //If the movie was not found, send 404 Not Found
                    String response = "{ \"error\": \"Movie not found\" }";
                    ApiUtils.sendResponse(exchange, 404, response);
                }
                /** END CHANGE for EX2 **/
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    /**
     * Manual JSON helpers (no external libraries)
     */
    private String movieToJson(Movie m) { //Convert a Movie object to a JSON string manually (no external library)
        return "{ \"id\": \"" + m.getId() + "\", "
                + "\"title\": \"" + m.getTitle() + "\", "
                + "\"genre\": \"" + m.getGenre() + "\", "
                + "\"releaseYear\": " + m.getReleaseYear() + " }";
    }

    /**
     * Converts a list of Movie objects to a JSON array string. 1 movie per line.
     */
    private String moviesToJson(List<Movie> movieList) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < movieList.size(); i++) {
            sb.append(movieToJson(movieList.get(i)));
            if (i < movieList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parses a Movie from a JSON string manually (no external library).
     * Expects keys: "id" (optional), "title", "genre", "releaseYear".
     */
    private Movie parseMovieFromJson(String json) {
        Movie movie = new Movie();
        // Remove outer braces and whitespace
        json = json.trim(); //Remove leading and trailing whitespace
        if (json.startsWith("{")) json = json.substring(1); //Remove leading brace if present
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1); //Remove trailing brace if present

        String[] pairs = json.split(","); //Split by commas
        for (String pair : pairs) { //Iterate through each pair
            String[] keyValue = pair.split(":", 2); //Split by colon
            if (keyValue.length != 2) continue; //Skip if not a valid pair

            String key = keyValue[0].trim().replace("\"", ""); //Remove quotes and whitespace from key
            String value = keyValue[1].trim().replace("\"", ""); //Remove quotes and whitespace from value

            switch (key) { //Map key to corresponding setter method
                case "id" -> movie.setId(UUID.fromString(value)); //Set id if present
                case "title" -> movie.setTitle(value); //Set title if present
                case "genre" -> movie.setGenre(value); //Set genre if present
                case "releaseYear" -> movie.setReleaseYear(Integer.parseInt(value)); //Set releaseYear if present
            }
        }
        return movie; //Return the parsed Movie object
    }
}

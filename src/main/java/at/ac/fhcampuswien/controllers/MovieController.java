package at.ac.fhcampuswien.controllers;


import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import at.ac.fhcampuswien.ApiUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MovieController implements HttpHandler {

    private final String BASE = "/api/movies/";
    private final List<Movie> movies = new ArrayList<>(Arrays.asList(Movie.generateDummyMovies()));

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleGetAll(method, exchange);
            case BASE + "add" -> handleAdd(method, exchange);
            case BASE + "delete" -> handleDelete(method, exchange);
            case BASE + "update" -> handleUpdate(method, exchange);
            default -> {
                DefaultUnknownMethode(exchange);
            }
        }
    }

    // --------------------GET ALL--------------------
    private void handleGetAll(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "GET" -> {
                String response = allMoviesToJson(movies);
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                DefaultUnknownMethode(exchange);
            }
        }
    }

    // --------------------ADD------------------
    private void handleAdd(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "POST" -> {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                String title = getJsonString(body, "title");
                String genre = getJsonString(body, "genre");
                Integer year = getJsonInt(body, "releaseYear");

                if (isBlank(title) || isBlank(genre) || year == null) {
                    ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                    return;
                }

                //Catch
                boolean exists = movies.stream().anyMatch(m ->
                        safeEquals(m.getTitle(), title) &&
                                safeEquals(m.getGenre(), genre) &&
                                m.getReleaseYear() == year
                );
                if (exists) {
                    ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
                    return;
                }

                movies.add(new Movie(title, genre, year));
                ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
            }
            default -> {
                DefaultUnknownMethode(exchange);
            }
        }
    }

    // --------------------DELETE--------------------
    private void handleDelete(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "DELETE" -> {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                String title = getJsonString(body, "title");
                String genre = getJsonString(body, "genre");
                Integer year = getJsonInt(body, "releaseYear");

                if (isBlank(title) || isBlank(genre) || year == null) {
                    ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                    return;
                }

                int idx = getIndexOfMovie(title, genre, year);
                if (idx == -1) {
                    ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
                    return;
                }

                movies.remove(idx);
                ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
            }
            default -> {
                DefaultUnknownMethode(exchange);
            }
        }
    }

    // --------------------UPDATE--------------------
    private void handleUpdate(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "PUT" -> {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                String idStr = getJsonString(body, "id");
                String title = getJsonString(body, "title");
                String genre = getJsonString(body, "genre");
                Integer year = getJsonInt(body, "releaseYear");

                //Catch
                if (isBlank(idStr) || isBlank(title) || isBlank(genre) || year == null) {
                    ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                    return;
                }

                UUID id;
                try {
                    id = UUID.fromString(idStr);
                } catch (IllegalArgumentException ex) {
                    ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                    return;
                }

                Movie movie = movies.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
                if (movie == null) {
                    ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
                    return;
                }

                movie.setTitle(title);
                movie.setGenre(genre);
                movie.setReleaseYear(year);

                ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
            }
            default -> {
                DefaultUnknownMethode(exchange);
            }
        }
    }

    // --------------------Helpers--------------------

    private int getIndexOfMovie(String title, String genre, int year) {
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            if (safeEquals(m.getTitle(), title) &&
                    safeEquals(m.getGenre(), genre) &&
                    m.getReleaseYear() == year) {
                return i;
            }
        }
        return -1;
    }

    private String getJsonString(String json, String key) {
        if (json == null) return null;
        String needle = "\"" + key + "\"";
        int k = json.indexOf(needle);
        if (k == -1) return null;

        int colon = json.indexOf(':', k + needle.length());
        if (colon == -1) return null;

        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote == -1) return null;

        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote == -1) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }

    private Integer getJsonInt(String json, String key) {
        if (json == null) return null;
        String needle = "\"" + key + "\"";
        int k = json.indexOf(needle);
        if (k == -1) return null;

        int colon = json.indexOf(':', k + needle.length());
        if (colon == -1) return null;

        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;

        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;

        if (end == start) return null;

        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String allMoviesToJson(List<Movie> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            Movie m = list.get(i);
            sb.append("{")
                    .append("\"id\":\"").append(m.getId()).append("\",")
                    .append("\"title\":\"").append(escape(m.getTitle())).append("\",")
                    .append("\"genre\":\"").append(escape(m.getGenre())).append("\",")
                    .append("\"releaseYear\":").append(m.getReleaseYear())
                    .append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    //--------Utils--------
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean safeEquals(String a, String b) {
        return a != null && a.equals(b);
    }

    private void DefaultUnknownMethode(HttpExchange exchange)throws IOException{
        String response = "{ \"error\": \"Method not allowed\" }";
        ApiUtils.sendResponse(exchange, 405, response);
    }
}

package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MovieController implements HttpHandler {

    private List<Movie> movies = Movie.generateDummyMovies();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        System.out.println("Received request: " + method + " to " + path);

        // --- 1. GET ALL MOVIES ---
        if (path.equals("/api/movies/getAll")) {
            if (method.equalsIgnoreCase("GET")) {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < movies.size(); i++) {
                    json.append(movies.get(i).toString());
                    if (i < movies.size() - 1) json.append(",");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        // --- 2. ADD MOVIE (POST) ---
        else if (path.equals("/api/movies/add")) {
            if (method.equalsIgnoreCase("POST")) {
                String body = getBody(exchange);
                String title = getJsonValue(body, "title");
                String genre = getJsonValue(body, "genre");
                String yearStr = getJsonValue(body, "releaseYear");

                if (title == null || genre == null || yearStr == null) {
                    sendResponse(exchange, 400, "{\"error\": \"Invalid movie data\"}");
                    return;
                }

                int year = Integer.parseInt(yearStr);

                boolean exists = false;
                for (Movie m : movies) {
                    if (m.getTitle().equalsIgnoreCase(title) && m.getGenre().equalsIgnoreCase(genre) && m.getReleaseYear() == year) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    sendResponse(exchange, 400, "{\"error\": \"Movie already exists\"}");
                } else {
                    movies.add(new Movie(title, genre, year));
                    sendResponse(exchange, 201, "{\"message\": \"Movie added successfully\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        // --- 3. DELETE MOVIE (DELETE) ---
        else if (path.equals("/api/movies/delete")) {
            if (method.equalsIgnoreCase("DELETE")) {
                String body = getBody(exchange);
                String title = getJsonValue(body, "title");
                String genre = getJsonValue(body, "genre");
                String yearStr = getJsonValue(body, "releaseYear");

                if (title == null || genre == null || yearStr == null) {
                    sendResponse(exchange, 400, "{\"error\": \"Invalid movie data\"}");
                    return;
                }

                int year = Integer.parseInt(yearStr);
                boolean removed = false;

                for (int i = 0; i < movies.size(); i++) {
                    Movie m = movies.get(i);
                    // trim() entfernt Leerzeichen, die Windows manchmal mitschickt
                    if (m.getTitle().trim().equalsIgnoreCase(title.trim()) &&
                            m.getGenre().trim().equalsIgnoreCase(genre.trim()) &&
                            m.getReleaseYear() == year) {
                        movies.remove(i);
                        removed = true;
                        break;
                    }
                }

                if (removed) {
                    sendResponse(exchange, 200, "{\"message\": \"Movie deleted successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\": \"Movie not found\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        // --- 4. UPDATE MOVIE (PUT) ---
        else if (path.equals("/api/movies/update")) {
            if (method.equalsIgnoreCase("PUT")) {
                String body = getBody(exchange);
                String id = getJsonValue(body, "id");
                String title = getJsonValue(body, "title");
                String genre = getJsonValue(body, "genre");
                String yearStr = getJsonValue(body, "releaseYear");

                if (id == null || title == null || genre == null || yearStr == null) {
                    sendResponse(exchange, 400, "{\"error\": \"Invalid movie data\"}");
                    return;
                }

                int year = Integer.parseInt(yearStr);
                boolean updated = false;

                for (Movie m : movies) {
                    if (m.getId().toString().equals(id)) {
                        m.setTitle(title);
                        m.setGenre(genre);
                        m.setReleaseYear(year);
                        updated = true;
                        break;
                    }
                }

                if (updated) {
                    sendResponse(exchange, 200, "{\"message\": \"Movie updated successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\": \"Movie not found\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        // --- 5. DEFAULT 404 ---
        else {
            sendResponse(exchange, 404, "{\"error\": \"Path not found\"}");
        }
    }

    // Hilfsfunktion: Liest den Text aus dem Request-Body
    private String getBody(HttpExchange exchange) {
        Scanner s = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    // Hilfsfunktion: Schickt die Antwort an den Client
    private void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = text.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    // Hilfsfunktion: Manuelles JSON-Parsing ohne Library
    private String getJsonValue(String json, String key) {
        try {
            String search = "\"" + key + "\":";
            int start = json.indexOf(search) + search.length();
            while (json.charAt(start) == ' ' || json.charAt(start) == '\"' || json.charAt(start) == ':') start++;
            int end = json.indexOf("\"", start);
            if (end == -1) end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).replace("\"", "").trim();
        } catch (Exception e) {
            return null;
        }
    }
}
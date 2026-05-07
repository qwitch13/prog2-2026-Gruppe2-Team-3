package at.ac.fhcampuswien;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ApiUtils {
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public static Map<String, String> parseQueryParams(String query) {
        // Create an empty map to store the extracted query parameters
        Map<String, String> params = new HashMap<>();

        // If the query is null or empty, return the empty map
        if (query == null || query.isBlank()) {
            return params;
        }

        // Example query:
        // "title=dark&genre=action&releaseYear=2008"
        // Split the query string by "&" to get separate key-value pairs
        // Result: ["title=dark", "genre=action", "releaseYear=2008"]
        String[] pairs = query.split("&");

        // Loop through every key-value pair
        for (String pair : pairs) {
            // Example pair:
            // "title=dark"
            // Split the pair by "=" into key and value
            // keyValue[0] = "title"
            // keyValue[1] = "dark"
            // The limit 2 makes sure that the split happens only once
            String[] keyValue = pair.split("=", 2);

            // Decode the key in case it contains URL-encoded characters
            // Example: "movie%20title" becomes "movie title"
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);

            // If a value exists, decode it
            // If no value exists, use an empty string
            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";

            // Store the key and value in the map
            params.put(key, value);
        }

        // Return the map with all extracted query parameters
        return params;
    }
}

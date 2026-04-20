package at.ac.fhcampuswien;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiUtils {
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public static Map<String, String> parseQueryParams(String query){
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isBlank()) {
            return params;
        }
        //Bsp "title=dark&genre=action&releaseYear=2008" -> 3 pairs
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            //Pair bsp: pair = "title=dark"
            String[] keyValue = pair.split("=", 2);
            //keyValue[0] = "title"
            //keyValue[1] = "dark"

            //Url decoder, for just in case
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";

            params.put(key, value);
        }

        return params;
    }

}

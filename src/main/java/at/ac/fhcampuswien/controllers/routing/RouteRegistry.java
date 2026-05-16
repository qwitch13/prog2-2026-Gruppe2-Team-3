package at.ac.fhcampuswien.controllers.routing;

import at.ac.fhcampuswien.ApiUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// open/closed support for controllers.
//
// before exercise 4 MovieController.handle() was a switch on path and each
// handler contained its own switch on http method. adding a new endpoint or a
// new method meant editing that file - the class was *not* closed for
// modification.
//
// RouteRegistry inverts that: routes are registered (path + method -> handler)
// at construction time. dispatch() walks the table and returns the appropriate
// 404 / 405 itself. adding a new endpoint = a new register(...) call;
// MovieController.handle() never needs to grow another case.
public final class RouteRegistry {

    // path -> (method -> handler). LinkedHashMap preserves registration order
    // which is convenient for debugging but otherwise irrelevant.
    private final Map<String, Map<String, RouteHandler>> routes = new LinkedHashMap<>();

    public RouteRegistry register(String method, String path, RouteHandler handler) {
        routes.computeIfAbsent(path, p -> new HashMap<>())
                .put(method.toUpperCase(), handler);
        return this;
    }

    // dispatches an incoming exchange to the matching handler.
    // returns 404 for an unknown path and 405 when the path is known but
    // the method is not registered for it.
    public void dispatch(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();

        Map<String, RouteHandler> byMethod = routes.get(path);
        if (byMethod == null) {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
            return;
        }

        RouteHandler handler = byMethod.get(method);
        if (handler == null) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        handler.handle(exchange);
    }
}

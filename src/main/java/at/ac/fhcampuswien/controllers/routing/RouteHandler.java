package at.ac.fhcampuswien.controllers.routing;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

// functional interface for a single route handler.
// kept separate from HttpHandler so that controllers can register
// handlers as method references / lambdas without implementing the
// full HttpHandler contract for every endpoint.
@FunctionalInterface
public interface RouteHandler {

    void handle(HttpExchange exchange) throws IOException;
}

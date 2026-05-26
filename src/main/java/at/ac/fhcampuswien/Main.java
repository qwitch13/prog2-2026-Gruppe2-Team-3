package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.database.DatabaseException;
import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Main {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Bring up the H2 schema before serving any traffic.
        try {
            DatabaseUtil.initializeDatabase();
        } catch (DatabaseException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return;
        }

        // Small smoke-test demo so the user can see the DB wiring works end to end.
        // Runs only when the application is started with -Dex3.demo=true.
        if (Boolean.getBoolean("ex3.demo")) {
            runDatabaseDemo();
        }

        // Create an HTTP server listening on the configured port.
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        // Register controllers and their handlers - REST endpoints.
        registerController(server, "/api/hello", new HelloController());
        registerController(server, "/api/movies/", new MovieController());

        // Start the server.
        server.setExecutor(null);
        server.start();
        System.out.printf("Server is running on http://localhost:%d%n", SERVER_PORT);
    }

    // Helper method to register a controller with its handler.
    private static void registerController(HttpServer server, String path, HttpHandler handler) {
        HttpContext context = server.createContext(path, handler);
        // Optionally add more configurations to context if needed.
    }

    // Demonstration sequence used to verify the repository wiring locally.
    // Runs only when the application is started with -Dex3.demo=true.
    private static void runDatabaseDemo() {
        MovieService service = new MovieService(new MovieRepository());

        try {
            Movie inception = new Movie("Inception", "Sci-Fi", 2010);
            Movie interstellar = new Movie("Interstellar", "Sci-Fi", 2014);

            service.addMovie(inception);
            service.addMovie(interstellar);

            System.out.println("[demo] after add:");
            print(service.getAllMovies());

            inception.setTitle("Inception (Director's Cut)");
            service.updateMovie(inception);

            System.out.println("[demo] after update:");
            print(service.getAllMovies());

            service.deleteMovie(interstellar);

            System.out.println("[demo] after delete:");
            print(service.getAllMovies());

        } catch (DatabaseException | MovieNotFoundException e) {
            System.err.println("[demo] failed: " + e.getMessage());
        }
    }

    private static void print(List<Movie> movies) {
        movies.forEach(m -> System.out.println("  - " + m));
    }
}
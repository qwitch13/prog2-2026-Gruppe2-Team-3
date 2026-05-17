package at.ac.fhcampuswien.exceptions;

// checked exception thrown when an sql operation or db connection fails.
// using a checked exception (extends Exception) makes db failures visible in
// every signature that touches the repository, so the controller has no chance
// to forget about translating them into a proper http response.
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

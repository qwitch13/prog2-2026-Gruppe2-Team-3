package at.ac.fhcampuswien.exceptions;

// checked exception thrown when a requested movie is not present in the database.
// extending Exception (and not RuntimeException) forces the caller to acknowledge
// the failure case in the method signature, which makes the contract between
// repository, service and controller explicit.
public class MovieNotFoundException extends Exception {

    public MovieNotFoundException(String message) {
        super(message);
    }

    public MovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

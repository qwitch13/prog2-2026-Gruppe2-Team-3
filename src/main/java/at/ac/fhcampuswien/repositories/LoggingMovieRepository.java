package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

// decorator pattern (structural).
// LoggingMovieRepository implements MovieRepository and delegates to another
// MovieRepository, adding a log line before and after every call.
// because the decorator and the delegate share the same interface, callers
// can compose them freely - e.g. new LoggingMovieRepository(new JdbcMovieRepository())
// or even new LoggingMovieRepository(new CachingMovieRepository(...)) - without
// any of the wrapped classes knowing they are being decorated. this is the
// behavioural extension the open/closed principle asks for: we add behaviour
// (logging) without modifying any existing repository code.
public class LoggingMovieRepository implements MovieRepository {

    private final MovieRepository delegate;

    public LoggingMovieRepository(MovieRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Movie> findAll() throws DatabaseException {
        log("findAll()");
        List<Movie> result = delegate.findAll();
        log("findAll() returned " + result.size() + " movies");
        return result;
    }

    @Override
    public void add(Movie movie) throws DatabaseException {
        log("add(" + movie + ")");
        delegate.add(movie);
    }

    @Override
    public boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException {
        log("delete(" + movie + ")");
        return delegate.delete(movie);
    }

    @Override
    public boolean update(Movie movie) throws DatabaseException, MovieNotFoundException {
        log("update(" + movie + ")");
        return delegate.update(movie);
    }

    // single place where the log format lives. easy to swap for a real logger later.
    private void log(String message) {
        System.out.println("[repo] " + message);
    }
}

package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

// read-only side of the persistence contract.
// kept separate from MovieWriter so that components which only need to
// query movies (for example a future "search service") do not depend on
// methods that can mutate the store. this is the textbook example of the
// interface segregation principle in this code base.
public interface MovieReader {

    List<Movie> findAll() throws DatabaseException;
}

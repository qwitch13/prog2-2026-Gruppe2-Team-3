package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

// write side of the persistence contract.
// components that only insert / update / delete (for example an admin import
// job) depend on MovieWriter and stay completely unaware of read methods.
// together with MovieReader this implements interface segregation.
public interface MovieWriter {

    void add(Movie movie) throws DatabaseException;

    boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException;

    boolean update(Movie movie) throws DatabaseException, MovieNotFoundException;
}

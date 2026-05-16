package at.ac.fhcampuswien.repositories;

// umbrella interface that combines the read and write side of the persistence
// contract for components that legitimately need both (for example MovieService).
// thanks to the segregated MovieReader / MovieWriter interfaces, callers can
// still depend on the smallest interface they need - this one only exists to
// avoid noisy "MovieReader & MovieWriter" type annotations in higher layers.
//
// the dependency inversion principle is realised through this interface:
// higher-level modules (MovieService, MovieController) depend on this
// abstraction, never on the concrete JdbcMovieRepository or any decorator.
public interface MovieRepository extends MovieReader, MovieWriter {
}

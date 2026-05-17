# Services Package

Contains business logic for movie operations and data coordination.

## Components

### MovieService
Orchestrates movie operations and coordinates between controllers and the repository layer.

**Key Methods:**
- `getAllMovies()` - Fetch all movies from database
- `searchMovies(title, genre, releaseYear)` - Search with optional filters
- `addMovie(movie)` - Persist a new movie
- `deleteMovie(movie)` - Remove a movie from database
- `updateMovie(movie)` - Modify an existing movie
- `movieExists(movie)` - Check for duplicate movies

**Responsibilities:**
- Encapsulates business logic for movie operations
- Validates data before persistence
- Handles communication with the repository layer
- Provides boolean return values from delete/update operations
- Manages the Movie model lifecycle

**Exception Handling:**
- All methods propagate `SQLException` from the repository to the controller
- Controllers handle exceptions and map them to appropriate HTTP responses

## Architecture Pattern

The service layer follows the **Service Layer** pattern:
1. Controllers delegate business logic to services
2. Services coordinate repository access and business rules
3. Services are testable in isolation by injecting mock repositories
4. Clear separation between HTTP concerns and business logic
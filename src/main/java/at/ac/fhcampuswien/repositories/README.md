# Repositories Package

Abstracts data access and persistence operations for the database layer.

## Components

### MovieRepository
Implements data access for movie operations.

**Key Methods:**
- `getAll()` - Retrieve all movies from database
- `search(title, genre, releaseYear)` - Query movies with filters
- `add(movie)` - Insert a new movie
- `delete(movie)` - Remove a movie
- `update(movie)` - Modify an existing movie
- `exists(movie)` - Check if a movie already exists

**Responsibilities:**
- Abstracts database interactions using H2 SQL database
- Handles SQL query execution
- Translates between Movie objects and database records
- Throws `DatabaseException` for persistence errors
- Throws `MovieNotFoundException` when a movie is not found

**Exception Handling:**
- `DatabaseException` - Wraps SQL or connection errors
- `MovieNotFoundException` - Thrown when delete/update targets non-existent movie

## Database Connection

The repository:
- Obtains connections from `DatabaseUtil.getConnection()`
- Uses standard JDBC operations
- Manages connection lifecycle (no connection pooling at this scale)
- Executes SQL queries and maps results to Movie objects

## Design Pattern

The Repository pattern:
- Provides a consistent interface for data access
- Isolates services from database implementation details
- Enables easy testing via mock repositories
- Facilitates switching database implementations later
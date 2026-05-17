# Repositories Package

Abstracts data access and persistence operations for the database layer.

## Components

### MovieRepository
Implements data access for movie operations.

**Key Methods:**
- `findAll()` - Retrieve all movies from database
- `add(movie)` - Insert a new movie
- `delete(movie)` - Remove a movie (returns boolean)
- `update(movie)` - Modify an existing movie (returns boolean)

**Responsibilities:**
- Abstracts database interactions using H2 SQL database
- Handles SQL query execution
- Translates between Movie objects and database records
- Returns boolean for delete/update to indicate success/failure
- All methods throw `SQLException` for database errors

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
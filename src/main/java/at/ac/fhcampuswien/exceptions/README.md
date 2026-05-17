# Exceptions Package

Defines custom exceptions for domain-specific error handling.

## Components

### DatabaseException
Thrown when database operations fail.

**Usage:**
- Wraps SQL errors, connection failures, and query execution problems
- Caught in controllers and mapped to HTTP 500 Internal Server Error
- Indicates server-side issues that require investigation

**Scenarios:**
- Database connection failure
- SQL syntax errors
- Constraint violations (unique key, etc.)
- Table lock or timeout issues

### MovieNotFoundException
Thrown when a movie operation targets a non-existent movie.

**Usage:**
- Raised by repository when delete/update fails because movie doesn't exist
- Caught in controllers and mapped to HTTP 404 Not Found
- Indicates the client requested an invalid resource

**Scenarios:**
- DELETE request for movie that's already deleted
- UPDATE request for movie with invalid ID
- Search returns no results (not an exception case, returns empty list)

## Exception Flow

1. **Repository Layer:** Catches SQL/DB errors, throws `DatabaseException` or `MovieNotFoundException`
2. **Service Layer:** Passes exceptions through to controllers
3. **Controller Layer:** Catches exceptions and maps to HTTP status codes:
   - `MovieNotFoundException` → 404
   - `DatabaseException` → 500
   - `JsonSyntaxException` → 400
   - Unexpected exceptions → 500 (prevents stack trace leaks)

## Design Benefits

Custom exceptions:
- Provide semantic meaning (not just generic errors)
- Enable type-based exception handling at appropriate layers
- Decouple domain logic from HTTP details
- Allow clear testing of error conditions
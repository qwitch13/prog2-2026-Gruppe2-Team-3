# Models Package

Defines domain objects and utility classes for data representation.

## Components

### Movie
The core domain model representing a movie.

**Fields:**
- `id` (Integer) - Unique identifier for the movie
- `title` (String) - Movie title
- `genre` (String) - Movie genre/category
- `releaseYear` (int) - Year the movie was released

**Purpose:**
- Serialized/deserialized by Gson for HTTP requests/responses
- Used throughout the application for data transfer
- Validated in controllers before persistence

### DummyGenerator
Utility class for generating test/dummy movie data.

**Purpose:**
- Creates sample movies for testing and demonstration
- Provides realistic movie data without external dependencies
- Used during development and testing phases

## Usage

Movie objects are created when:
1. Parsing JSON request bodies in controllers
2. Retrieving data from the database via repositories
3. Returning API responses to clients
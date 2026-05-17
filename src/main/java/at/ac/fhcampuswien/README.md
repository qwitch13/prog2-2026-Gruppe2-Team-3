# Movie API - Exercise 3

A RESTful API for managing movies built with Java's built-in HTTP server.

## API Endpoints

### Hello API (`/api/hello/`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hello/` | Base endpoint, returns a welcome message |
| GET | `/api/hello/greet` | Returns a greeting message |
| POST | `/api/hello/greet` | Echoes back the received request body |
| GET | `/api/hello/info` | Returns API information |

**Example Requests:**
```bash
# Get greeting
curl http://localhost:8080/api/hello/greet

# Send and echo data
curl -X POST http://localhost:8080/api/hello/greet \
  -H "Content-Type: application/json" \
  -d '{"name": "World"}'
```

---

### Movie API (`/api/movies/`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/movies/getAll` | Retrieve all movies |
| GET | `/api/movies/search` | Search movies by query parameters |
| POST | `/api/movies/add` | Add a new movie |
| DELETE | `/api/movies/delete` | Delete a movie |
| PUT | `/api/movies/update` | Update an existing movie |

#### GET `/api/movies/getAll`
Returns all movies in the database.

**Response:** 200 OK
```json
[
  {
    "id": 1,
    "title": "The Matrix",
    "genre": "Sci-Fi",
    "releaseYear": 1999
  }
]
```

#### GET `/api/movies/search`
Search movies by title, genre, and/or release year.

**Query Parameters:**
- `title` (optional) - Movie title to search
- `genre` (optional) - Movie genre to filter
- `releaseYear` (optional) - Release year to filter

**Example:**
```bash
curl "http://localhost:8080/api/movies/search?title=Matrix&genre=Sci-Fi&releaseYear=1999"
```

**Response:** 200 OK
```json
[
  {
    "id": 1,
    "title": "The Matrix",
    "genre": "Sci-Fi",
    "releaseYear": 1999
  }
]
```

#### POST `/api/movies/add`
Add a new movie to the database.

**Request Body:**
```json
{
  "title": "Inception",
  "genre": "Sci-Fi",
  "releaseYear": 2010
}
```

**Response:**
- 201 Created: `{ "message": "Movie added successfully" }`
- 400 Bad Request: Invalid or duplicate movie
- 500 Internal Server Error: Database error

#### DELETE `/api/movies/delete`
Delete a movie from the database.

**Request Body:**
```json
{
  "title": "Inception",
  "genre": "Sci-Fi",
  "releaseYear": 2010
}
```

**Response:**
- 200 OK: `{ "message": "Movie deleted successfully" }`
- 404 Not Found: Movie not found
- 400 Bad Request: Invalid movie data
- 500 Internal Server Error: Database error

#### PUT `/api/movies/update`
Update an existing movie.

**Request Body:**
```json
{
  "id": 1,
  "title": "The Matrix Reloaded",
  "genre": "Sci-Fi",
  "releaseYear": 2003
}
```

**Response:**
- 200 OK: `{ "message": "Movie updated successfully" }`
- 404 Not Found: Movie not found
- 400 Bad Request: Invalid movie data
- 500 Internal Server Error: Database error

## Error Handling

All endpoints return standardized error responses:
```json
{
  "error": "Error description here"
}
```

**HTTP Status Codes:**
- `200 OK` - Successful GET/DELETE/PUT
- `201 Created` - Successful POST
- `400 Bad Request` - Invalid request data or JSON syntax
- `404 Not Found` - Resource not found
- `405 Method Not Allowed` - Wrong HTTP method for endpoint
- `500 Internal Server Error` - Database or server error

## Project Structure

See individual README files in each package directory for detailed documentation.
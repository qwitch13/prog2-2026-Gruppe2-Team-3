# Controllers Package

Contains HTTP request handlers that dispatch incoming requests to the appropriate service methods.

## Components

### HelloController
HTTP handler for the `/api/hello/` endpoint group.

**Features:**
- Base endpoint at `/api/hello/` returning a welcome message
- `/api/hello/greet` for greeting functionality (GET and POST)
- `/api/hello/info` for API information

**Request Handling:**
- Dispatches requests by path
- Validates HTTP methods (returns 405 if unsupported)
- Returns JSON responses with appropriate status codes

### MovieController
HTTP handler for the `/api/movies/` endpoint group.

**Features:**
- Full CRUD operations for movies
- Search functionality with query parameters
- Input validation for movie data
- Exception mapping to HTTP status codes:
  - `MovieNotFoundException` → 404
  - `DatabaseException` → 500
  - `JsonSyntaxException` → 400
  - Other exceptions → 500 (prevents stack trace leaks)

**Endpoints:**
- `GET /api/movies/getAll` - Retrieve all movies
- `GET /api/movies/search` - Search with filters
- `POST /api/movies/add` - Create new movie
- `DELETE /api/movies/delete` - Remove movie
- `PUT /api/movies/update` - Modify existing movie

## Architecture

Both controllers:
1. Implement `HttpHandler` interface (Java's built-in HTTP server)
2. Receive requests via `handle(HttpExchange exchange)`
3. Parse HTTP method and path
4. Route to appropriate handler method
5. Catch exceptions and return proper error responses
6. Send responses via `ApiUtils.sendResponse()`

The separation of concerns allows controllers to focus on routing and error handling, while business logic resides in the `services` package.
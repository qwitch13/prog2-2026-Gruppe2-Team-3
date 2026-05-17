# Database Package

Handles database initialization and connection management.

## Components

### DatabaseUtil
Utility class for database setup and connection management.

**Key Responsibilities:**
- Initializes the H2 in-memory database on application startup
- Creates the `movies` table with proper schema
- Provides `getConnection()` method for acquiring database connections
- Manages single database instance for the application

**Database Schema:**
```sql
CREATE TABLE movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  genre VARCHAR(100) NOT NULL,
  releaseYear INT NOT NULL
)
```

**Features:**
- Uses H2 database (lightweight, no server required)
- In-memory storage (data persists during application lifetime)
- Automatic table creation on first run
- JDBC connection handling

## Usage

The repository layer calls `DatabaseUtil.getConnection()` to:
1. Get a connection to execute SQL queries
2. Execute SELECT, INSERT, UPDATE, DELETE statements
3. Return the connection when done

## Database Lifecycle

- **Initialization:** Table and constraints are created when `DatabaseUtil` is first instantiated
- **Runtime:** Connections are created on-demand for each database operation
- **Shutdown:** Data is lost when the application terminates (in-memory database)
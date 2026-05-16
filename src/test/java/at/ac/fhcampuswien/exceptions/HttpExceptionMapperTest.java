package at.ac.fhcampuswien.exceptions;

import at.ac.fhcampuswien.exceptions.HttpExceptionMapper.HttpError;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// verifies the chain-of-responsibility behaviour of HttpExceptionMapper:
// each rule maps a specific exception type to an HttpError, and an
// unregistered exception falls through to the default 500.
class HttpExceptionMapperTest {

    @Test
    void mapsMovieNotFoundTo404() {
        HttpExceptionMapper mapper = HttpExceptionMapper.defaultMapper();

        HttpError result = mapper.map(new MovieNotFoundException("missing"));

        assertEquals(404, result.status());
        assertTrue(result.body().contains("missing"));
    }

    @Test
    void mapsDatabaseExceptionTo500() {
        HttpExceptionMapper mapper = HttpExceptionMapper.defaultMapper();

        HttpError result = mapper.map(new DatabaseException("boom"));

        assertEquals(500, result.status());
        assertTrue(result.body().contains("Database error"));
    }

    @Test
    void mapsJsonSyntaxExceptionTo400() {
        HttpExceptionMapper mapper = HttpExceptionMapper.defaultMapper();

        HttpError result = mapper.map(new JsonSyntaxException("bad json"));

        assertEquals(400, result.status());
        assertTrue(result.body().contains("Malformed JSON"));
    }

    @Test
    void unregisteredExceptionFallsBackTo500() {
        HttpExceptionMapper mapper = HttpExceptionMapper.defaultMapper();

        HttpError result = mapper.map(new IllegalStateException("anything"));

        assertEquals(500, result.status());
        assertTrue(result.body().contains("Internal server error"));
    }

    @Test
    void customRuleTakesPrecedenceOverFallback() {
        HttpExceptionMapper mapper = new HttpExceptionMapper()
                .register(IllegalArgumentException.class,
                        e -> new HttpError(418, "{ \"error\": \"" + e.getMessage() + "\" }"));

        HttpError result = mapper.map(new IllegalArgumentException("teapot"));

        assertEquals(418, result.status());
        assertTrue(result.body().contains("teapot"));
    }
}
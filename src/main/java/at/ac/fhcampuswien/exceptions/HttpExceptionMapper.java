package at.ac.fhcampuswien.exceptions;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// behavioural pattern: strategy registry / chain of responsibility.
//
// before exercise 4 every controller handler had its own try/catch block that
// repeated the same "MovieNotFoundException -> 404 / DatabaseException -> 500 /
// JsonSyntaxException -> 400 / anything else -> 500" logic. that violated SRP
// (the controller knew both about http routing and about how to translate
// every domain exception) and OCP (adding a new exception type meant editing
// each handler).
//
// HttpExceptionMapper centralises the translation. each entry is a small
// strategy (Function<E, HttpError>). they are tried in registration order and
// the first matching one decides the result. callers register new entries
// without modifying the mapper itself - that is the open/closed part.
public final class HttpExceptionMapper {

    // immutable description of an http error response.
    public record HttpError(int status, String body) {
    }

    // single strategy entry: which exception type to match and how to translate it.
    private static final class Mapping<E extends Exception> {
        final Class<E> type;
        final Function<E, HttpError> mapper;

        Mapping(Class<E> type, Function<E, HttpError> mapper) {
            this.type = type;
            this.mapper = mapper;
        }

        boolean matches(Exception e) {
            return type.isInstance(e);
        }

        HttpError apply(Exception e) {
            return mapper.apply(type.cast(e));
        }
    }

    private final List<Mapping<?>> mappings = new ArrayList<>();
    private Function<Exception, HttpError> fallback =
            e -> new HttpError(500, errorJson("Internal server error"));

    // registers a translation rule for a specific exception type.
    public <E extends Exception> HttpExceptionMapper register(
            Class<E> type, Function<E, HttpError> mapper) {
        mappings.add(new Mapping<>(type, mapper));
        return this;
    }

    // replaces the fallback used when no rule matched.
    public HttpExceptionMapper fallback(Function<Exception, HttpError> fallback) {
        this.fallback = fallback;
        return this;
    }

    // chain-of-responsibility dispatch: walk the registered rules in order
    // and return the first match. if none matches, the fallback decides.
    public HttpError map(Exception exception) {
        for (Mapping<?> mapping : mappings) {
            if (mapping.matches(exception)) {
                return mapping.apply(exception);
            }
        }
        return fallback.apply(exception);
    }

    // factory for the default rules used by MovieController.
    public static HttpExceptionMapper defaultMapper() {
        return new HttpExceptionMapper()
                .register(MovieNotFoundException.class,
                        e -> new HttpError(404, errorJson(e.getMessage())))
                .register(DatabaseException.class,
                        e -> new HttpError(500, errorJson("Database error: " + e.getMessage())))
                .register(JsonSyntaxException.class,
                        e -> new HttpError(400, errorJson("Malformed JSON: " + e.getMessage())));
    }

    // wraps an error message in the canonical json envelope used across the api.
    public static String errorJson(String message) {
        String safe = message == null ? "" : message.replace("\"", "\\\"");
        return "{ \"error\": \"" + safe + "\" }";
    }
}

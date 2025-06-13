package exceptions;

/**
 * Exception thrown when an HTTP request cannot be parsed correctly.
 * This can occur due to malformed request lines, headers, or body.
 */
public class HTTPParseException extends Exception {

    public HTTPParseException(String message) {
        super(message);
    }
    
    public HTTPParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

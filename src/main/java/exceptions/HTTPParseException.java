package exceptions;

public class HTTPParseException extends Exception {

    public HTTPParseException(String message) {
        super(message);
    }
    
    public HTTPParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

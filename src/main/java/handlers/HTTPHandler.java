package handlers;

import http.HTTPResponse;
import http.HTTPRequest;

/**
 * Interface for handling HTTP requests.
 */
public interface HTTPHandler {

    public HTTPResponse handle(HTTPRequest request);

}

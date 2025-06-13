package handlers;

import http.HTTPRequest;
import http.HTTPResponse;
import http.HTTPResponses;

/**
 * Handler for 404 Not Found responses.
 */
public class NotFoundHandler implements HTTPHandler {

    @Override
    public HTTPResponse handle(HTTPRequest request) {
        return HTTPResponses.notFoundError(request.getVersion(), "404 Not Found");
    }

}

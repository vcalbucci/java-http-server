package handlers;

import http.HTTPRequest;
import http.HTTPResponse;
import http.HTTPResponses;

public class NotFoundHandler implements HTTPHandler {

    @Override
    public HTTPResponse handle(HTTPRequest request) {
        return HTTPResponses.notFoundError(request);
    }

}

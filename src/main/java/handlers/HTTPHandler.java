package handlers;

import http.HTTPRequest;

public interface HTTPHandler {

    public HTTPHandler handle(HTTPRequest request);

}

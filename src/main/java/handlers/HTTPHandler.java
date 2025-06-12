package handlers;

import http.HTTPResponse;
import http.HTTPRequest;

public interface HTTPHandler {

    public HTTPResponse handle(HTTPRequest request);

}

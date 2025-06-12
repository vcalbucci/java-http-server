package handlers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import http.HTTPRequest;
import http.HTTPResponse;

public class UserAgentHandler implements HTTPHandler {

    public HTTPResponse handle(HTTPRequest request) {

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "Unknown";
        }

        byte[] bodyBytes = ("Your User-Agent is: " + userAgent).getBytes(StandardCharsets.UTF_8);

        return new HTTPResponse(
                request.getVersion(),
                200,
                "OK",
                Map.of("Content-Type", "text/plain", "Content-Length", String.valueOf(bodyBytes.length)),
                bodyBytes);

    }

}

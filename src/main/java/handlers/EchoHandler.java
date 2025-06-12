package handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import http.ContentType;
import http.HTTPRequest;
import http.HTTPResponse;
import http.HTTPResponses;
import util.CompressionUtils;

public class EchoHandler implements HTTPHandler {

    @Override
    public HTTPResponse handle(HTTPRequest request) {
        byte[] body = request.getPath().substring("/echo/".length()).getBytes(StandardCharsets.UTF_8);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.TEXT_PLAIN.getType());

        if (CompressionUtils.acceptsGzip(request)) {
            try {
                body = CompressionUtils.gzipCompress(body);
                headers.put("Content-Encoding", "gzip");
            } catch (IOException e) {
                return HTTPResponses.internalServerError(
                        request.getVersion(),
                        "Failed to compress response body: " + e.getMessage());
            }
        }

        headers.put("Content-Length", String.valueOf(body.length));

        return new HTTPResponse(
                request.getVersion(),
                200,
                "OK",
                headers,
                body);
    }
}

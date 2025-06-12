package handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

        List<String> compressionTypes = new ArrayList<>();

        if (request.getHeader("Accept-Encoding") != null) {
            compressionTypes = Arrays.asList(request.getHeader("Accept-Encoding").split(",\\s*"));
            for (String type : compressionTypes) {
                if (type.startsWith("gzip")) {
                    headers.put("Content-Encoding", "gzip");
                    try {
                        body = CompressionUtils.gzipCompress(body);
                    } catch (IOException e) {
                        return HTTPResponses.internalServerError(
                                request.getVersion(),
                                "Failed to compress response body: " + e.getMessage());
                    }
                    break;
                }
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

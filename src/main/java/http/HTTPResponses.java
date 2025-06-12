package http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HTTPResponses {

    public static HTTPResponse internalServerError(String version, String message) {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length", String.valueOf(body.length));

        return new HTTPResponse(
                version,
                500,
                "Internal Server Error",
                headers,
                body);
    }

    public static HTTPResponse notFoundError(String version, String message) {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.TEXT_PLAIN.getType());
        headers.put("Content-Length", String.valueOf(body.length));

        return new HTTPResponse(
                version,
                404,
                "Not Found",
                headers,
                body);

    }

    public static HTTPResponse conflictError(String version, String message) {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length", String.valueOf(body.length));

        return new HTTPResponse(
                version,
                409,
                "Conflict",
                headers,
                body);
    }

    public static HTTPResponse created(String version, String message) {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length", String.valueOf(body.length));

        return new HTTPResponse(
                version,
                201,
                "Created",
                headers,
                body);
    }

}

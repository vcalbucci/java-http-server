package http;

import java.util.Map;

/**
 * Represents an HTTP response to be sent back to the client.
 * Stores status line, headers, and response body.
 * Used by handlers to construct protocol-compliant HTTP responses.
 */
public class HTTPResponse {

    String version;
    int statusCode;
    String reasonPhrase;
    Map<String, String> headers;
    byte[] body;

    public HTTPResponse(String version, int statusCode, String reasonPhrase,
            Map<String, String> headers, byte[] body) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

}

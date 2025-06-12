package http;

import java.util.Map;

public class HTTPResponse {

    String version;
    int statusCode;
    String statusMessage;
    Map<String, String> headers;
    byte[] body;

    public HTTPResponse(String version, int statusCode, String statusMessage,
            Map<String, String> headers, byte[] body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

}

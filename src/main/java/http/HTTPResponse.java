package http;

import java.util.HashMap;

public class HTTPResponse {

    String version;
    int statusCode;
    String statusMessage;
    HashMap<String, String> headers;
    byte[] body;

    public HTTPResponse(String version, int statusCode, String statusMessage,
            HashMap<String, String> headers, byte[] body) {
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

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

}

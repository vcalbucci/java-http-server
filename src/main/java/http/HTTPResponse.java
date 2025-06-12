package http;

import java.util.Map;

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

    public byte[] getBody() {
        return body;
    }

}

package http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import exceptions.HTTPParseException;
import util.IOUtils;

/**
 * Parses an HTTP request from an InputStream.
 * Provides the parsed request as an HTTPRequest object for further processing.
 * Supports parsing of request line, headers, and body.
 */
public class HTTPRequestParser {

    private InputStream in;

    public HTTPRequestParser(InputStream in) {
        this.in = in;
    }

    public HTTPRequest nextRequest() throws HTTPParseException {
        try {
            String requestLine = IOUtils.readLine(in);
            if (requestLine == null || requestLine.isEmpty()) {
                return null;
            }
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                throw new HTTPParseException("Malformed request line: " + requestLine);
            }
            String method = requestParts[0];
            String path = requestParts[1];
            String version = requestParts[2];
            HashMap<String, String> headers = readHeaders();

            byte[] body = readBody(
                    headers.get("Content-Length") != null ? Integer.parseInt(headers.get("Content-Length")) : 0);

            return new HTTPRequest(method, path, version, body, headers);

        } catch (IOException e) {
            throw new HTTPParseException("Failed to parse HTTP request:", e);
        }
    }

    private HashMap<String, String> readHeaders() throws HTTPParseException {
        HashMap<String, String> headers = new HashMap<>();
        try {
            String line = IOUtils.readLine(in);
            while (!line.isEmpty()) {
                int colonIndex = line.indexOf(":");
                if (colonIndex != -1) {
                    String headerName = line.substring(0, colonIndex).trim();
                    String headerValue = line.substring(colonIndex + 1).trim();
                    headers.put(headerName, headerValue);
                }
                line = IOUtils.readLine(in);
            }

        } catch (IOException e) {
            throw new HTTPParseException("Failed to read HTTP request headers:", e);
        }
        return headers;
    }

    private byte[] readBody(int length) throws HTTPParseException {
        byte[] body = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            try {
                int read = in.read(body, bytesRead, length - bytesRead);
                if (read == -1) {
                    break;
                }
                bytesRead += read;
            } catch (IOException e) {
                throw new HTTPParseException("Failed to read HTTP request body:", e);
            }
        }
        return body;
    }

}

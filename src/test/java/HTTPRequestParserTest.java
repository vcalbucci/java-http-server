import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import exceptions.HTTPParseException;
import http.HTTPRequest;
import http.HTTPRequestParser;

public class HTTPRequestParserTest {

    @Test
    void testSimpleGetRequest() throws Exception {
        String input = 
            "GET /hello HTTP/1.1\r\n" +
            "Host: example.com\r\n" +
            "\r\n";

        HTTPRequest request = parse(input);

        assertEquals("GET", request.getMethod());
        assertEquals("/hello", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("example.com", request.getHeader("Host"));
        assertEquals(0, request.getBody().length);
    }

    @Test
    void testPostRequest() throws Exception {
        String input = 
            "POST /submit HTTP/1.1\r\n" +
            "Host: example.com\r\n" +
            "Content-Length: 5\r\n" +
            "\r\n" +
            "abcde";

        HTTPRequest request = parse(input);

        assertEquals("POST", request.getMethod());
        assertEquals("/submit", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("example.com", request.getHeader("Host"));
        assertEquals(5, request.getBody().length);
        assertEquals("abcde", new String(request.getBody(), StandardCharsets.UTF_8));
    }

    @Test
    void testNoHeadersNoBody() throws Exception {
        String input = 
            "GET /plain HTTP/1.1\r\n" +
            "\r\n";

        HTTPRequest request = parse(input);

        assertEquals("GET", request.getMethod());
        assertEquals("/plain", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals(0, request.getBody().length);
    }

    @Test
    void testMalformedRequestLine() {
        String input = 
            "INVALIDREQUEST\r\n" +
            "\r\n";

        assertThrows(HTTPParseException.class, () -> {
            HTTPRequestParser parser = new HTTPRequestParser(toInputStream(input));
            parser.nextRequest();
        });
    }

    @Test
    void testEmptyStream() throws Exception {
        String input = "";

        HTTPRequestParser parser = new HTTPRequestParser(toInputStream(input));
        HTTPRequest request = parser.nextRequest();

        assertNull(request);
    }

    private static HTTPRequest parse(String input) throws HTTPParseException {
        HTTPRequestParser parser = new HTTPRequestParser(toInputStream(input));
        HTTPRequest request = parser.nextRequest();
        assertNotNull(request, "Expected HTTPRequest but got null");
        return request;
    }

    private static ByteArrayInputStream toInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }
}

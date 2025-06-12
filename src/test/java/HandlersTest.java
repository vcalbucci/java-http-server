import http.ContentType;
import http.HTTPRequest;
import http.HTTPResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import handlers.EchoHandler;
import handlers.FileHandler;
import handlers.NotFoundHandler;
import handlers.UserAgentHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class HandlersTest {

    @Test
    public void testNotFoundHandler() {
        NotFoundHandler handler = new NotFoundHandler();

        HTTPRequest request = new HTTPRequest("GET", "/does-not-exist", "HTTP/1.1", new byte[0], new HashMap<>());
        HTTPResponse response = handler.handle(request);

        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getReasonPhrase());
        assertEquals(ContentType.TEXT_PLAIN.getType(), response.getHeader("Content-Type"));
        assertNotNull(response.getBody());
    }

    @Test
    public void testUserAgentHandler() {
        UserAgentHandler handler = new UserAgentHandler();

        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "TestAgent");

        HTTPRequest request = new HTTPRequest("GET", "/user-agent", "HTTP/1.1", new byte[0], headers);
        HTTPResponse response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("TestAgent"));
    }

    @Test
    public void testEchoHandlerWithoutCompression() {
        EchoHandler handler = new EchoHandler();

        HTTPRequest request = new HTTPRequest("GET", "/echo/hello-world", "HTTP/1.1", new byte[0], new HashMap<>());
        HTTPResponse response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertEquals("hello-world", body);
        assertEquals(ContentType.TEXT_PLAIN.getType(), response.getHeader("Content-Type"));
    }

    @Test
    public void testEchoHandlerWithCompression() {
        EchoHandler handler = new EchoHandler();

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        HTTPRequest request = new HTTPRequest("GET", "/echo/hello-world", "HTTP/1.1", new byte[0], headers);
        HTTPResponse response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        assertEquals(ContentType.TEXT_PLAIN.getType(), response.getHeader("Content-Type"));
        assertEquals("gzip", response.getHeader("Content-Encoding"));
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0); // compressed body should not be empty
    }

    @Test
    public void testFileHandlerGET(@TempDir Path tempDir) throws IOException {
        // Arrange → create test file
        Path testFile = tempDir.resolve("testfile.txt");
        String fileContent = "Hello, file!";
        Files.write(testFile, fileContent.getBytes(StandardCharsets.UTF_8));

        // Create handler
        FileHandler handler = new FileHandler(tempDir.toString());

        // Build request
        HTTPRequest request = new HTTPRequest("GET", "/testfile.txt", "HTTP/1.1", new byte[0], new HashMap<>());
        HTTPResponse response = handler.handle(request);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        assertEquals("application/octet-stream", response.getHeader("Content-Type"));
        assertEquals(fileContent, new String(response.getBody(), StandardCharsets.UTF_8));
    }

    @Test
    public void testFileHandlerPOST(@TempDir Path tempDir) {
        // Arrange → content to write
        String fileContent = "POST body content";
        byte[] bodyBytes = fileContent.getBytes(StandardCharsets.UTF_8);

        // Create handler
        FileHandler handler = new FileHandler(tempDir.toString());

        // Build request
        HTTPRequest request = new HTTPRequest("POST", "/newfile.txt", "HTTP/1.1", bodyBytes, new HashMap<>());
        HTTPResponse response = handler.handle(request);

        // Assert → 201 Created
        assertEquals(201, response.getStatusCode());
        assertEquals("Created", response.getReasonPhrase());

        // Check that file was written correctly
        Path writtenFile = tempDir.resolve("newfile.txt");
        assertTrue(Files.exists(writtenFile));
        try {
            String readContent = Files.readString(writtenFile, StandardCharsets.UTF_8);
            assertEquals(fileContent, readContent);
        } catch (IOException e) {
            fail("Failed to read written file: " + e.getMessage());
        }
    }
}

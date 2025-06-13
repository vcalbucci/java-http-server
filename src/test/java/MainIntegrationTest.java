import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import main.Main;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainIntegrationTest {

    private static ExecutorService serverExecutor;

    @BeforeAll
    public static void startServer(@TempDir Path tempDir) {
        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> Main.main(new String[] { tempDir.toString() }));

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    @AfterAll
    public static void stopServer() {
        Main.stopServer();
        serverExecutor.shutdownNow();
    }

    @Test
    @Order(1)
    public void testRootReturns404() throws IOException {
        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("404"));
        }
    }

    @Test
    @Order(2)
    public void testUserAgentHandler() throws IOException {
        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("GET /user-agent HTTP/1.1\r\nHost: localhost\r\nUser-Agent: MyTestAgent\r\n\r\n".getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));

            while (!reader.readLine().isEmpty()) {
            }

            char[] bodyChars = new char[100];
            int charsRead = reader.read(bodyChars);
            String body = new String(bodyChars, 0, charsRead);
            assertTrue(body.contains("MyTestAgent"));
        }
    }

    @Test
    @Order(3)
    public void testEchoHandlerWithoutCompression() throws IOException {
        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("GET /echo/test-message HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));

            int contentLength = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
                }
            }

            // Read the body using the content length
            char[] bodyChars = new char[contentLength];
            int charsRead = reader.read(bodyChars);
            String body = new String(bodyChars, 0, charsRead);
            assertEquals("test-message", body);
        }
    }

    @Test
    @Order(4)
    public void testEchoHandlerWithCompression() throws IOException {
        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("GET /echo/test-compressed HTTP/1.1\r\nHost: localhost\r\nAccept-Encoding: gzip\r\n\r\n"
                    .getBytes());
            out.flush();

            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1)
                    break;
                responseBuffer.write(buffer, 0, bytesRead);

                byte[] responseBytes = responseBuffer.toByteArray();
                String responseStr = new String(responseBytes, StandardCharsets.UTF_8);
                int headerEndIndex = responseStr.indexOf("\r\n\r\n");

                if (headerEndIndex != -1) {
                    String headers = responseStr.substring(0, headerEndIndex);
                    String[] headerLines = headers.split("\r\n");

                    boolean sawGzipHeader = false;
                    int contentLength = 0;

                    assertTrue(headerLines[0].contains("200"));

                    for (String line : headerLines) {
                        if (line.toLowerCase().startsWith("content-encoding: gzip")) {
                            sawGzipHeader = true;
                        }
                        if (line.toLowerCase().startsWith("content-length:")) {
                            contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
                        }
                    }

                    assertTrue(sawGzipHeader);
                    assertTrue(contentLength > 0, "Content-Length should be set");

                    int headerLength = headerEndIndex + 4;
                    int bodyBytesAlreadyRead = responseBytes.length - headerLength;

                    while (bodyBytesAlreadyRead < contentLength) {
                        int bytesRead2 = in.read(buffer);
                        if (bytesRead2 == -1)
                            break;
                        responseBuffer.write(buffer, 0, bytesRead2);
                        bodyBytesAlreadyRead += bytesRead2;
                    }

                    byte[] finalResponse = responseBuffer.toByteArray();
                    int actualBodyLength = finalResponse.length - headerLength;
                    assertEquals(contentLength, actualBodyLength, "Expected to read full body");
                    break;
                }
            }

        }
    }

    @Test
    @Order(5)
    public void testFileHandlerPOSTAndGET() throws IOException {
        String filename = "testfile.txt";
        String fileContent = "Hello from POST body!";

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = String.format(
                    "POST /files/%s HTTP/1.1\r\nHost: localhost\r\nContent-Length: %d\r\n\r\n%s",
                    filename, fileContent.length(), fileContent);

            out.write(request.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("201"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(("GET /files/" + filename + " HTTP/1.1\r\nHost: localhost\r\n\r\n").getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));

            while (!reader.readLine().isEmpty()) {
            }

            char[] bodyChars = new char[100];
            int charsRead = reader.read(bodyChars);
            String body = new String(bodyChars, 0, charsRead);
            assertEquals(fileContent, body);
        }

    }

    @Test
    @Order(6)
    public void testHeadRequest() throws IOException {
        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("HEAD /echo/test-head HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n".getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));

            boolean sawContentLength = false;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    sawContentLength = true;
                }
            }

            assertTrue(sawContentLength);
            assertEquals(-1, in.read());
        }
    }

    @Test
    @Order(7)
    public void testFileHandlerPUT(@TempDir Path tempDir) throws IOException {
        String filename = "testputfile.txt";
        String initialContent = "Initial PUT content";
        String updatedContent = "Updated PUT content";

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = String.format(
                    "PUT /files/%s HTTP/1.1\r\nHost: localhost\r\nContent-Length: %d\r\n\r\n%s",
                    filename, initialContent.length(), initialContent);

            out.write(request.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("201") || statusLine.contains("200"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = String.format(
                    "PUT /files/%s HTTP/1.1\r\nHost: localhost\r\nContent-Length: %d\r\n\r\n%s",
                    filename, updatedContent.length(), updatedContent);

            out.write(request.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200") || statusLine.contains("201"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(("GET /files/" + filename + " HTTP/1.1\r\nHost: localhost\r\n\r\n").getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));

            while (!reader.readLine().isEmpty()) {
            }

            char[] bodyChars = new char[200];
            int charsRead = reader.read(bodyChars);
            String body = new String(bodyChars, 0, charsRead);
            assertEquals(updatedContent, body);
        }

    }

    @Test
    @Order(8)
    public void testFileHandlerDELETE(@TempDir Path tempDir) throws IOException {
        String filename = "testdeletefile.txt";
        String fileContent = "File to be deleted";

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = String.format(
                    "PUT /files/%s HTTP/1.1\r\nHost: localhost\r\nContent-Length: %d\r\n\r\n%s",
                    filename, fileContent.length(), fileContent);

            out.write(request.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("201") || statusLine.contains("200"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(("DELETE /files/" + filename + " HTTP/1.1\r\nHost: localhost\r\n\r\n").getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("200"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(("DELETE /files/" + filename + " HTTP/1.1\r\nHost: localhost\r\n\r\n").getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("404"));
        }

        try (Socket socket = new Socket("localhost", 1212)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(("GET /files/" + filename + " HTTP/1.1\r\nHost: localhost\r\n\r\n").getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String statusLine = reader.readLine();
            assertNotNull(statusLine);
            assertTrue(statusLine.contains("404"));
        }
    }

}

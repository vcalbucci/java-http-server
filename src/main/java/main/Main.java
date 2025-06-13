package main;

import http.HTTPRequest;
import http.HTTPRequestParser;
import http.HTTPResponse;
import http.Router;
import handlers.EchoHandler;
import handlers.FileHandler;
import handlers.UserAgentHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entry point for the HTTP server.
 * Listens for incoming connections and processes HTTP requests using the
 * configured Router.
 */
public class Main {

    private static volatile boolean keepRunning = true;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1212;
        String fileBaseDir = args.length > 0 ? args[0] : ".";

        Router router = new Router();
        router.addRoute("GET", "/user-agent", new UserAgentHandler());
        router.addRoute("GET", "/echo/", new EchoHandler());
        router.addRoute("POST", "/files/", new FileHandler(fileBaseDir));
        router.addRoute("GET", "/files/", new FileHandler(fileBaseDir));

        ExecutorService pool = Executors.newFixedThreadPool(20);

        try (ServerSocket server = new ServerSocket(port)) {
            serverSocket = server;
            server.setReuseAddress(true);
            System.out.println("Server started on port " + port);

            while (keepRunning) {
                try {
                    Socket client = server.accept();
                    pool.submit(() -> handleClient(client, router));
                } catch (SocketException e) {
                    if (!keepRunning) {
                        break;
                    } else {
                        throw e;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        } finally {
            pool.shutdown();
            System.out.println("Server shutting down.");
        }
    }

    public static void stopServer() {
        keepRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    private static void handleClient(Socket client, Router router) {
        try (InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream()) {

            HTTPRequestParser parser = new HTTPRequestParser(in);

            boolean keepAlive = true;

            while (keepAlive) {
                HTTPRequest request = parser.nextRequest();
                if (request == null) {
                    break;
                }

                HTTPResponse response = router.route(request).handle(request);

                writeResponse(out, response);

                String connectionHeader = request.getHeader("Connection");
                if (connectionHeader != null && connectionHeader.equalsIgnoreCase("close")) {
                    keepAlive = false;
                }
            }

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void writeResponse(OutputStream out, HTTPResponse response) throws IOException {
        out.write((response.getVersion() + " " + response.getStatusCode() + " " + response.getReasonPhrase() + "\r\n")
                .getBytes(StandardCharsets.UTF_8));

        if (response.getHeader("Connection") == null) {
            out.write("Connection: keep-alive\r\n".getBytes(StandardCharsets.UTF_8));
        }

        for (var entry : response.getHeaders().entrySet()) {
            out.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }

        out.write("\r\n".getBytes(StandardCharsets.UTF_8));

        if (response.getBody() != null && response.getBody().length > 0) {
            out.write(response.getBody());
        }

        out.flush();
    }

}

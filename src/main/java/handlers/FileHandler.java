package handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import http.HTTPRequest;
import http.HTTPResponse;
import http.HTTPResponses;
import util.CompressionUtils;
import util.FileUtils;

/**
 * FileHandler is an HTTP handler that serves files from a specified base
 * directory.
 * It supports both GET, POST, PUT, and DELETE methods and compresses responses
 * using gzip if
 * the client accepts it.
 */
public class FileHandler implements HTTPHandler {

    private final String baseDirectory;

    public FileHandler(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public HTTPResponse handle(HTTPRequest request) {
        String relativePath = request.getPath().startsWith("/files/")
                ? request.getPath().substring("/files/".length())
                : request.getPath();
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        File file = new File(baseDirectory, relativePath);
        Path path = Paths.get(file.getAbsolutePath());

        switch (request.getMethod()) {
            case "GET":
                return handleGET(request, file, path);
            case "POST":
                return handlePOST(request, file, path);
            case "PUT":
                if (!file.exists()) {
                    return handlePOST(request, file, path);
                }
                return handlePUT(request, file, path);
            case "DELETE":
                return handleDELETE(request, file, path);
            case "OPTIONS":
                return handleOPTIONS(request);
            default:
                return HTTPResponses.notFoundError(request.getVersion(),
                        "Unsupported method: " + request.getMethod());
        }
    }

    private HTTPResponse handleGET(HTTPRequest request, File file, Path path) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/octet-stream");

        try {
            if (file.exists() && file.isFile()) {
                byte[] data = Files.readAllBytes(path);
                headers.put("Content-Length", String.valueOf(data.length));
                if (CompressionUtils.acceptsGzip(request)) {
                    data = CompressionUtils.gzipCompress(data);
                    headers.put("Content-Length", String.valueOf(data.length));
                    headers.put("Content-Encoding", "gzip");
                }
                return new HTTPResponse(
                        request.getVersion(),
                        200,
                        "OK",
                        headers,
                        data);
            } else {
                return HTTPResponses.notFoundError(request.getVersion(),
                        "File not found: " + file.getPath());
            }
        } catch (IOException e) {
            return HTTPResponses.internalServerError(
                    request.getVersion(),
                    "Failed to read file: " + e.getMessage());
        }
    }

    private HTTPResponse handlePOST(HTTPRequest request, File file, Path path) {
        if (!file.exists()) {
            FileUtils.createFile(file.getPath(), request.getBody());
            return HTTPResponses.created(request.getVersion(), "File created: " + file.getPath());
        } else {
            return HTTPResponses.conflictError(
                    request.getVersion(),
                    "File already exists: " + file.getPath());
        }
    }

    private HTTPResponse handlePUT(HTTPRequest request, File file, Path path) {
        HashMap<String, String> headers = new HashMap<>();

        try {
            Files.write(path, request.getBody());
            return new HTTPResponse(
                    request.getVersion(),
                    200,
                    "OK",
                    headers,
                    ("File updated: " + file.getPath()).getBytes());
        } catch (IOException e) {
            return HTTPResponses.internalServerError(
                    request.getVersion(),
                    "Failed to write file: " + e.getMessage());
        }
    }

    private HTTPResponse handleDELETE(HTTPRequest request, File file, Path path) {
        HashMap<String, String> headers = new HashMap<>();

        if (file.exists() && file.isFile()) {
            try {
                Files.delete(path);
                return new HTTPResponse(
                        request.getVersion(),
                        200,
                        "OK",
                        headers,
                        ("File deleted: " + file.getPath()).getBytes());
            } catch (IOException e) {
                return HTTPResponses.internalServerError(
                        request.getVersion(),
                        "Failed to delete file: " + e.getMessage());
            }
        } else {
            return HTTPResponses.notFoundError(request.getVersion(),
                    "File not found: " + file.getPath());
        }
    }

    private HTTPResponse handleOPTIONS(HTTPRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Allow", "GET, POST, PUT, DELETE, HEAD, OPTIONS");
        
        return new HTTPResponse(
                request.getVersion(),
                204,
                "No Content",
                headers,
                new byte[0]);
    }
}

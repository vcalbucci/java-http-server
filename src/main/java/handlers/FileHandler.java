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

public class FileHandler implements HTTPHandler {

    private final String baseDirectory;

    public FileHandler(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public HTTPResponse handle(HTTPRequest request) {

        HashMap<String, String> headers = new HashMap<>();
        File file = new File(baseDirectory, request.getPath());
        Path path = Paths.get(file.getAbsolutePath());

        if (request.getMethod().equals("GET")) {
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
        } else {
            if (!file.exists()) {
                FileUtils.createFile(file.getPath(), request.getBody());
                return HTTPResponses.created(request.getVersion(), "File created: " + file.getPath());
            } else {
                return HTTPResponses.conflictError(
                        request.getVersion(),
                        "File already exists: " + file.getPath());
            }

        }
    }

}

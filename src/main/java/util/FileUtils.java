package util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

/**
 * Utility class for file operations.
 */
public class FileUtils {

    public static void createFile(String path, byte[] content) {
        try {
            Files.write(
                    Paths.get(path),
                    content,
                    StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + path, e);
        }
    }
}

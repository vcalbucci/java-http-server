package util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for reading lines from an InputStream.
 */
public class IOUtils {

    public static String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\r') {
                in.read();
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }
    

}
package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import http.HTTPRequest;

public class CompressionUtils {

    public static byte[] gzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
        gzipOut.write(data);
        gzipOut.finish();
        gzipOut.close();
        return byteOut.toByteArray();
    }

    public static boolean acceptsGzip(HTTPRequest request) {
        String acceptEncoding = request.getHeader("Accept-Encoding");
        if (acceptEncoding == null) {
            return false;
        }
        List<String> encodingTypes = Arrays.asList(acceptEncoding.split(",\\s*"));
        for (String type : encodingTypes) {
            String encodingName = type.split(";")[0].trim();
            if (encodingName.equalsIgnoreCase("gzip")) {
                return true;
            }
        }
        return false;
    }

}

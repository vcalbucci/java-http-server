package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

    public static byte[] gzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
        gzipOut.write(data);
        gzipOut.finish();
        gzipOut.close();
        return byteOut.toByteArray();
    }

}

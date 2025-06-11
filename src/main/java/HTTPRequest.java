
import java.util.HashMap;

public class HTTPRequest {
    
    private String method;
    private String path;
    private String version;
    private byte[] body;
    private HashMap<String, String> headers;

    public HTTPRequest(String method, String path, String version, byte[] body, HashMap<String, String> headers) {
        this.method = method;
        this.path = path;
        this.body = body;
        this.version = version;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }   
    public String getPath() {
        return path;
    }
    public String getVersion() {
        return version;
    }
    public byte[] getBody() {
        return body;
    }
    public String getHeader(String name) {
        return headers.getOrDefault(name, null);
    }
}

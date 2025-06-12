package http;

public enum ContentType {
    TEXT_CSS("text/css"),
    IMAGE_GIF("image/gif"),
    TEXT_HTML("text/html"),
    IMAGE_X_ICON("image/x-icon"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

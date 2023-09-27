package gauth.exception;

public class JsonNotParseException extends RuntimeException {
    public JsonNotParseException(Throwable cause) {
        super("Json can't parse", cause);
    }
}
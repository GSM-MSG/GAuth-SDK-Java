package gauth.exception;

public class InvalidEncodingException extends RuntimeException{
    public InvalidEncodingException(Throwable cause) {
        super("this encoding is not valid", cause);
    }
}

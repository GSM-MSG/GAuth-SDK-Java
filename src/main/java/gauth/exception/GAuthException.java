package gauth.exception;

public class GAuthException extends RuntimeException{
    private final Integer code;

    public GAuthException(Integer code) {
        super(code.toString());
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

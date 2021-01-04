package hr.documentcloud.exception;

public class FIleStoringException extends RuntimeException {

    public FIleStoringException() {
    }

    public FIleStoringException(String message) {
        super(message);
    }

    public FIleStoringException(String message, Throwable cause) {
        super(message, cause);
    }

    public FIleStoringException(Throwable cause) {
        super(cause);
    }

    public FIleStoringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

package hr.documentcloud.exception;

public class FileStoringException extends RuntimeException {

    public FileStoringException() {
    }

    public FileStoringException(String message) {
        super(message);
    }

    public FileStoringException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStoringException(Throwable cause) {
        super(cause);
    }

    public FileStoringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

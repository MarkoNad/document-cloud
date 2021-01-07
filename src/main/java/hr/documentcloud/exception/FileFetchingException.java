package hr.documentcloud.exception;

public class FileFetchingException extends RuntimeException {

    public FileFetchingException() {
    }

    public FileFetchingException(String message) {
        super(message);
    }

    public FileFetchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFetchingException(Throwable cause) {
        super(cause);
    }

    public FileFetchingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

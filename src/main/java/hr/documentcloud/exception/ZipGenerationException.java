package hr.documentcloud.exception;

public class ZipGenerationException extends RuntimeException {

    public ZipGenerationException() {
    }

    public ZipGenerationException(String message) {
        super(message);
    }

    public ZipGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZipGenerationException(Throwable cause) {
        super(cause);
    }

    public ZipGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

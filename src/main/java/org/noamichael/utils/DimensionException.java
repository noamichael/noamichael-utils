package org.noamichael.utils;

/**
 *
 * @author michael
 */
public class DimensionException extends RuntimeException{

    public DimensionException() {
    }

    public DimensionException(String message) {
        super(message);
    }

    public DimensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DimensionException(Throwable cause) {
        super(cause);
    }

    public DimensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}

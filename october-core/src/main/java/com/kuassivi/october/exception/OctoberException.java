package com.kuassivi.october.exception;

import com.kuassivi.october.rx.OctoberSubscriber;

/**
 * Default October Exception.
 * <p>
 * This exception wraps your custom IO Exceptions if you use the {@link OctoberSubscriber}, so you
 * should retrieve those specific IO Exception through {@link #getCause()} method.
 */
public class OctoberException extends Exception {

    /**
     * Constructs a new {@code OctoberException} with its stack trace filled in.
     */
    public OctoberException() {
    }

    /**
     * Constructs a new {@code OctoberException} with its stack trace and detail message filled in.
     *
     * @param detailMessage the detail message for this exception.
     */
    public OctoberException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new instance of this class with detail message and cause filled in.
     *
     * @param message The detail message for the exception.
     * @param cause   The detail cause for the exception.
     */
    public OctoberException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new instance of this class with its detail cause filled in.
     *
     * @param cause The detail cause for the exception.
     */
    public OctoberException(Throwable cause) {
        super(cause == null
              ? null
              : cause.toString(), cause);
    }
}

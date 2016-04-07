package com.kuassivi.october.exception;

import java.io.IOException;

/**
 * Default IO October Exception.
 * <p>
 * Usually, you should inherit from this class in your custom inner IO Exceptions like those thrown
 * by a HTTP connection call.
 */
public class OctoberIOException extends IOException {

    /**
     * Constructs a new {@code OctoberIOException} with its stack trace filled in.
     */
    public OctoberIOException() {
    }

    /**
     * Constructs a new {@code OctoberIOException} with its stack trace and detail message filled
     * in.
     *
     * @param detailMessage the detail message for this exception.
     */
    public OctoberIOException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new instance of this class with detail message and cause filled in.
     *
     * @param message The detail message for the exception.
     * @param cause   The detail cause for the exception.
     */
    public OctoberIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new instance of this class with its detail cause filled in.
     *
     * @param cause The detail cause for the exception.
     */
    public OctoberIOException(Throwable cause) {
        super(cause == null
              ? null
              : cause.toString(), cause);
    }
}

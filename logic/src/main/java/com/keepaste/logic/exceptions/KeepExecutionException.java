package com.keepaste.logic.exceptions;

import lombok.NonNull;

public class KeepExecutionException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message   the error message
     */
    public KeepExecutionException(@NonNull final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message   the error message
     * @param cause     the cause
     */
    public KeepExecutionException(@NonNull final String message, @NonNull final Throwable cause) {
        super(message, cause);
    }
}

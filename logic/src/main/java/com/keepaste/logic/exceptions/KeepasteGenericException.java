package com.keepaste.logic.exceptions;

import lombok.NonNull;

public class KeepasteGenericException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message   the error message
     * @param ex        the cause
     */
    public KeepasteGenericException(@NonNull final String message, @NonNull final Throwable ex) {
        super(message, ex);
    }
}

package com.keepaste.logic.exceptions;

import lombok.NonNull;

public class KeepParameterExecutionException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message   the error message
     */
    public KeepParameterExecutionException(@NonNull final String message) {
        super(message);
    }
}

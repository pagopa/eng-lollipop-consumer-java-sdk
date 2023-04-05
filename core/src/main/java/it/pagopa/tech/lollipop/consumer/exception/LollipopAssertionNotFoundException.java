package it.pagopa.tech.lollipop.consumer.exception;

import java.util.Objects;

/**
 * Thrown in case of problems retrieving assertion
 */
public class LollipopAssertionNotFoundException extends Exception {

    /**
     * Constructs new exception with provided message and cause
     *
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public LollipopAssertionNotFoundException(String message, Throwable cause) {
            super(message, cause);
    }

}

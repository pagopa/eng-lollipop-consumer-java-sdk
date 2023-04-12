package it.pagopa.tech.lollipop.consumer.exception;

/** Thrown when the given instant is not a valid timestamp */
public class InvalidInstantFormatException extends Exception {

    /**
     * Constructs new exception with provided message
     *
     * @param message Detail message
     */
    public InvalidInstantFormatException(String message) {
        super(message);
    }
}

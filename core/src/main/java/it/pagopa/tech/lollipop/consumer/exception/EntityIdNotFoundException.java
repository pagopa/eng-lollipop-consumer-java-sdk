package it.pagopa.tech.lollipop.consumer.exception;

public class EntityIdNotFoundException extends Exception{

    /**
     * Constructs new exception with provided messag
     *
     * @param message Detail message
     */
    public EntityIdNotFoundException(String message) {
        super(message);
    }
}

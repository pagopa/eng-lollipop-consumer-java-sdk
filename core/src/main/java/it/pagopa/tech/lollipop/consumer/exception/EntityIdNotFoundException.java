/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.exception;

/** Thrown in case of problems finding the right data for the given entityId */
public class EntityIdNotFoundException extends Exception {

    /**
     * Constructs new exception with provided message
     *
     * @param message Detail message
     */
    public EntityIdNotFoundException(String message) {
        super(message);
    }
}

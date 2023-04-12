package it.pagopa.tech.lollipop.consumer.exception;

public class CertDataTagListNotFoundException extends Exception{
    /**
     * Constructs new exception with provided message and cause
     *
     * @param message Detail message
     * @param cause Exception causing the constructed one
     */
    public CertDataTagListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

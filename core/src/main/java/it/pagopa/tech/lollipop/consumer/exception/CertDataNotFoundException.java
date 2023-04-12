package it.pagopa.tech.lollipop.consumer.exception;

public class CertDataNotFoundException extends Exception{
    /**
     * Constructs new exception with provided message and cause
     *
     * @param message Detail message
     * @param cause Exception causing the constructed one
     */
    public CertDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

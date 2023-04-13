/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.exception;

/** Thrown in case an oidc assertion is retrieved, oidc assertions are not supported yet */
public class OidcAssertionNotSupported extends Exception {

    /**
     * Constructs new exception with provided message and cause
     *
     * @param message Detail message
     */
    public OidcAssertionNotSupported(String message) {
        super(message);
    }
}

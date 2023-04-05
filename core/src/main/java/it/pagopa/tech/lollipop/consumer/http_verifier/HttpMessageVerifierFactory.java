/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier;

/** Interface for the factory used to create instances of {@link HttpMessageVerifier} */
public interface HttpMessageVerifierFactory {

    /** @return instance of HttpMessageVerifier */
    HttpMessageVerifier create();
}

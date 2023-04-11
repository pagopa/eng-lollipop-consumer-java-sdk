/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

/**
 * Interface of the storage used for storing the assertion retrieved for validation
 */
public interface AssertionStorage {

    /**
     * Retrieve the assertion associated with the provided assertion reference
     * @param assertionRef the assertion reference
     * @return the SAML assertion if found, otherwise null
     */
    SamlAssertion getAssertion(String assertionRef);

    /**
     * Store the provided assertion
     * @param assertionRef the assertion reference
     * @param assertion the SAML assertion
     */
    void saveAssertion(String assertionRef, SamlAssertion assertion);
}

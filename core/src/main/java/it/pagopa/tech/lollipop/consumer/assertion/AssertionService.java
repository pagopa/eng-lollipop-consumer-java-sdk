/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion;

import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.OidcAssertionNotSupported;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

/** Interface of the assertion service, it defines the methods for managing the assertions */
public interface AssertionService {

    /**
     * Retrieve a SAML assertion using the provided jwt and the assertion reference
     *
     * @param jwt the jwt
     * @param assertionRef the assertion reference
     * @return the requested SAML assertion or null if the assertion is not supported
     * @throws LollipopAssertionNotFoundException if some error occurred retrieving the request
     */
    SamlAssertion getAssertion(String jwt, String assertionRef)
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported;
}

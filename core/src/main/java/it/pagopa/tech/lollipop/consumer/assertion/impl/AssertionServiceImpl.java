/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import javax.inject.Inject;

/** Service for managing the assertion */
public class AssertionServiceImpl implements AssertionService {

    private final AssertionStorage assertionStorage;
    private final AssertionClient assertionClient;

    @Inject
    public AssertionServiceImpl(
            AssertionStorage assertionStorage, AssertionClient assertionClient) {
        this.assertionStorage = assertionStorage;
        this.assertionClient = assertionClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieve the SAML assertion, first looking in the storage if enabled ({@link
     * it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig}) and then if not found
     * through the client {@link AssertionClient}. If the storage is enabled ({@link
     * it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig}) and the assertion is
     * retrieved through the client, it store the assertion.
     *
     * @param jwt the jwt
     * @param assertionRef the assertion reference
     * @return the SAML assertion or null if the assertion is not supported (not SAML)
     * @throws LollipopAssertionNotFoundException if some error occurred retrieving the assertion
     *     through the client
     */
    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef)
            throws LollipopAssertionNotFoundException {

        if (jwt == null || jwt.isBlank() || assertionRef == null || assertionRef.isBlank()) {
            String errMsg =
                    String.format(
                            "Cannot retrieve the assertion, jwt [%s] or assertion reference [%s]"
                                    + " missing",
                            jwt, assertionRef);
            throw new IllegalArgumentException(errMsg);
        }

        SamlAssertion samlAssertion = assertionStorage.getAssertion(assertionRef);

        if (samlAssertion != null) {
            return samlAssertion;
        }

        samlAssertion = assertionClient.getAssertion(jwt, assertionRef);

        if (samlAssertion == null) {
            return null;
        }

        assertionStorage.saveAssertion(assertionRef, samlAssertion);

        return samlAssertion;
    }
}

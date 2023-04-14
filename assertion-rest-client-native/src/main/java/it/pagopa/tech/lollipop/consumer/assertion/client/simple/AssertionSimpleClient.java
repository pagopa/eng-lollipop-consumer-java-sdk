/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.AssertionRef;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.LCUserInfo;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.OidcUserInfo;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.SamlUserInfo;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.OidcAssertionNotSupported;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import javax.inject.Inject;

/** Implementation of the @AssertionClient using generated openAPI REST Client */
public class AssertionSimpleClient implements AssertionClient {

    private final DefaultApi defaultApi;

    @Inject
    public AssertionSimpleClient(ApiClient client) {
        this.defaultApi = new DefaultApi(client);
    }

    /**
     * Retrieve assertion from IdentityProvider using REST Client The retrieved assertion can be of
     * two types: SAML or OIDC Only SAML assertions are supported at this moment
     *
     * @param jwt Auth token for header param x-pagopa-lollipop-auth
     * @param assertionRef Assertion unique identification
     * @return the retrieved assertion or null if the assertion is not supported (not SAML)
     * @throws LollipopAssertionNotFoundException if some error occurred in the request
     * @throws OidcAssertionNotSupported if the assertion retrieved is a OIDC token
     */
    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef)
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        AssertionRef ref = new AssertionRef(assertionRef);

        LCUserInfo responseAssertion;

        try {
            responseAssertion = this.defaultApi.getAssertion(ref, jwt);
        } catch (ApiException e) {
            throw new LollipopAssertionNotFoundException(
                    "Error retrieving assertion: " + e.getMessage(), e);
        }

        if (responseAssertion.getActualInstance().getClass().equals(SamlUserInfo.class)) {
            SamlAssertion response = new SamlAssertion();
            SamlUserInfo data = (SamlUserInfo) responseAssertion.getActualInstance();
            String assertionData = data.getResponseXml();
            response.setAssertionRef(assertionRef);
            response.setAssertionData(assertionData);
            return response;
        }
        if (responseAssertion.getActualInstance().getClass().equals(OidcUserInfo.class)) {
            throw new OidcAssertionNotSupported("OIDC Claims not supported yet.");
        }

        return null;
    }
}

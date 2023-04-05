/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

/**
 * Interface of the service to be used for validating SAML Assertions obtained using the content passed in the {@link LollipopConsumerRequest}
 */
public interface AssertionVerifierService {

    /**
     * Validates Lollipop request Assertion, using the assertion-ref within the request header params
     * @param request
     * @return
     */
    boolean validateLollipop(LollipopConsumerRequest request);
}

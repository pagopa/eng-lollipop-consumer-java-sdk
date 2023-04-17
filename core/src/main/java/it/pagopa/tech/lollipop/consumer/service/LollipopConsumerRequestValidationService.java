/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.exception.LollipopRequestContentValidationException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

/** Interface of the service used to verify Lollipop request header params */
public interface LollipopConsumerRequestValidationService {

    /**
     * Validates all request headers
     *
     * @param request the Lollipop request
     * @throws LollipopRequestContentValidationException if some error occurred during validation
     */
    void validateLollipopRequest(LollipopConsumerRequest request)
            throws LollipopRequestContentValidationException;
}

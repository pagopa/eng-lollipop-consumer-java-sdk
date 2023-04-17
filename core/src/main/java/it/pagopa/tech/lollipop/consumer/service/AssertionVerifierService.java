/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.exception.AssertionPeriodException;
import it.pagopa.tech.lollipop.consumer.exception.AssertionThumbprintException;
import it.pagopa.tech.lollipop.consumer.exception.AssertionUserIdException;
import it.pagopa.tech.lollipop.consumer.exception.ErrorRetrievingAssertionException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

/**
 * Interface of the service to be used for validating SAML Assertions obtained using the content
 * passed in the {@link LollipopConsumerRequest}
 */
public interface AssertionVerifierService {

    /**
     * Validates Lollipop request Assertion, using the assertion-ref within the request header
     * params
     *
     * @param request the Lollipop request
     * @return true if the assertion is valid
     * @throws ErrorRetrievingAssertionException thrown for general errors in the verification
     *     process
     * @throws AssertionPeriodException thrown for error in assertion period validation
     * @throws AssertionThumbprintException thrown for error in assertion thumbprint validation
     * @throws AssertionUserIdException thrown for error in user id validation
     */
    boolean validateLollipop(LollipopConsumerRequest request)
            throws ErrorRetrievingAssertionException, AssertionPeriodException,
                    AssertionThumbprintException, AssertionUserIdException;
}

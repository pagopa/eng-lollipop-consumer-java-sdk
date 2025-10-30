/* (C)2023-2025 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
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
     * @throws ErrorRetrievingAssertionException thrown for errors when retrieving the assertion
     * @throws AssertionPeriodException thrown for error in assertion period validation
     * @throws AssertionThumbprintException thrown for error in assertion thumbprint validation
     * @throws AssertionUserIdException thrown for error in user id validation
     * @throws ErrorRetrievingIdpCertDataException thrown for errors when retrieving the IDP data
     * @throws ErrorValidatingAssertionSignature thrown for error in signature validation
     * @throws AssertionNameException thrown for error in get name and surname validation
     */
    CommandResult validateLollipop(LollipopConsumerRequest request)
            throws ErrorRetrievingAssertionException, AssertionPeriodException,
                    AssertionThumbprintException, AssertionUserIdException,
                    ErrorRetrievingIdpCertDataException, ErrorValidatingAssertionSignature,
                    AssertionNameException;
}

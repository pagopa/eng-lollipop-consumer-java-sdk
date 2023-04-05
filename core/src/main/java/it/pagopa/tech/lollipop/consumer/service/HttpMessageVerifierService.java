/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.UnsupportedEncodingException;

/**
 * Interface of the service to be used for validating the http {@link LollipopConsumerRequest} through the digest and signature
 * validation of the http-signature draft
 */
public interface HttpMessageVerifierService {

    /**
     * Validates the http request
     * @param request
     * @return flag to true if the request is validated
     * @throws UnsupportedEncodingException
     * @throws LollipopDigestException
     * @throws LollipopVerifierException
     */
    boolean verifyHttpMessage(LollipopConsumerRequest request)
            throws UnsupportedEncodingException, LollipopDigestException, LollipopVerifierException;
}

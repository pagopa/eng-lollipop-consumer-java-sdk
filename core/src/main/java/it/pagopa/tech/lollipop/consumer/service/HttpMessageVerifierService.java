package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

import java.io.UnsupportedEncodingException;

public interface HttpMessageVerifierService {
    boolean verifyHttpMessage(LollipopConsumerRequest request) throws LollipopDigestException, UnsupportedEncodingException;

}

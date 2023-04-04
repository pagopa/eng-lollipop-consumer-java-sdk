package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

public interface HttpMessageVerifierService {

    boolean verifyHttpMessage(LollipopConsumerRequest request);
}

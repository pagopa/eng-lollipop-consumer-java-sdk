package it.pagopa.common.lollipop.consumer.service;

import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;

public interface HttpMessageVerifierService {

    boolean verifyHttpMessage(LollipopConsumerRequest request);
}

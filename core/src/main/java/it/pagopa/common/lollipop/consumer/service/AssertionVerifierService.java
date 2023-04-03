package it.pagopa.common.lollipop.consumer.service;

import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;

public interface AssertionVerifierService {

    boolean validateLollipop(LollipopConsumerRequest request);
}

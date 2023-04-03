package it.pagopa.common.lollipop.consumer.service.impl;

import it.pagopa.common.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.common.lollipop.consumer.service.HttpMessageVerifierService;

public class HttpMessageVerifierServiceImplStub implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest request) {
        return false;
    }
}

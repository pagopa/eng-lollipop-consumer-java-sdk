package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;

public class HttpMessageVerifierServiceImplStub implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest request) {
        return false;
    }
}

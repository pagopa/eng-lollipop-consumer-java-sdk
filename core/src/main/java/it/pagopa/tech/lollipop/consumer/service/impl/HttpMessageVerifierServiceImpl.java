package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;

import javax.inject.Inject;

public class HttpMessageVerifierServiceImpl implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;

    @Inject
    public HttpMessageVerifierServiceImpl(HttpMessageVerifier httpMessageVerifier) {
        this.httpMessageVerifier = httpMessageVerifier;
    }

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest request) {
        return false;
    }
}

package it.pagopa.spid.common.lc.service.impl;

import it.pagopa.spid.common.lc.http_verifier.HttpMessageVerifier;
import it.pagopa.spid.common.lc.model.LollipopConsumerRequest;
import it.pagopa.spid.common.lc.service.HttpMessageVerifierService;

public class HttpMessageVerifierServiceImplStub implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest request) {
        return false;
    }
}

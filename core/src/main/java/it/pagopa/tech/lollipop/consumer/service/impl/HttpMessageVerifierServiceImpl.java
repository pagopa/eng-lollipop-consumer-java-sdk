package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.HttpMessageVerifierConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

public class HttpMessageVerifierServiceImpl implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;
    private HttpMessageVerifierConfig httpMessageVerifierConfig;

    @Inject
    public HttpMessageVerifierServiceImpl(HttpMessageVerifier httpMessageVerifier,
                                          HttpMessageVerifierConfig httpMessageVerifierConfig) {
        this.httpMessageVerifier = httpMessageVerifier;
        this.httpMessageVerifierConfig = httpMessageVerifierConfig;
    }

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest lollipopConsumerRequest) throws LollipopDigestException {
        return verifyContentDigest(lollipopConsumerRequest) && verifyHttpSignature(lollipopConsumerRequest);
    }

    protected boolean verifyContentDigest(LollipopConsumerRequest lollipopConsumerRequest) throws LollipopDigestException {

        // Attempt to recover content-digest
        String contentDigest = lollipopConsumerRequest.getHeaderParams()
                .get(httpMessageVerifierConfig.getContentDigestHeader());

        if (contentDigest == null) {
            throw new LollipopDigestException
                    (LollipopDigestException.ErrorCode.MISSING_DIGEST,
                            "Missing required Content-Digest for validation");
        }

        //Attempt to execute digest validation
        try {
            if (!httpMessageVerifier.verifyDigest(
                    contentDigest,
                    lollipopConsumerRequest.getRequestBody(),
                    null)) {
                throw new LollipopDigestException(
                        LollipopDigestException.ErrorCode.INCORRECT_DIGEST,"Invalid Digest");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
    * TODO: stub
     */
    private boolean verifyHttpSignature(LollipopConsumerRequest lollipopConsumerRequest) {
        return true;
    }

}

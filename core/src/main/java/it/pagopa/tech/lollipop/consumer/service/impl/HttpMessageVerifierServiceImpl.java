package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class HttpMessageVerifierServiceImpl implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;
    private LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    @Inject
    public HttpMessageVerifierServiceImpl(HttpMessageVerifier httpMessageVerifier,
                                          LollipopConsumerRequestConfig lollipopConsumerRequestConfig) {
        this.httpMessageVerifier = httpMessageVerifier;
        this.lollipopConsumerRequestConfig = lollipopConsumerRequestConfig;
    }

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest lollipopConsumerRequest)
            throws LollipopDigestException, UnsupportedEncodingException {

        Map<String,String> headerParams = lollipopConsumerRequest.getHeaderParams();

        String signatureInput = headerParams.get(lollipopConsumerRequestConfig.getSignatureInputHeader());

        if (signatureInput == null) {

        }

        String contentDigest = null;

        String requestBody = null;

        String contentEncoding = null;

        return verifyContentDigest(contentDigest, requestBody, contentEncoding) && verifyHttpSignature(lollipopConsumerRequest);
    }

    protected boolean verifyContentDigest(String contentDigest, String requestBody, String contentEncoding)
            throws LollipopDigestException, UnsupportedEncodingException {



        if (contentDigest == null) {
            throw new LollipopDigestException
                    (LollipopDigestException.ErrorCode.MISSING_DIGEST,
                            "Missing required Content-Digest for validation");
        }


        //Attempt to execute digest validation
        if (!httpMessageVerifier.verifyDigest(
                contentDigest,
                requestBody,
                contentEncoding)) {
            throw new LollipopDigestException(
                    LollipopDigestException.ErrorCode.INCORRECT_DIGEST,"Invalid Digest");
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

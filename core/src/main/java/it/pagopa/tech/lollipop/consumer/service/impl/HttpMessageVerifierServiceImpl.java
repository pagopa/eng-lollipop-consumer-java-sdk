/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.inject.Inject;

public class HttpMessageVerifierServiceImpl implements HttpMessageVerifierService {

    private HttpMessageVerifier httpMessageVerifier;
    private LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    @Inject
    public HttpMessageVerifierServiceImpl(
            HttpMessageVerifier httpMessageVerifier,
            LollipopConsumerRequestConfig lollipopConsumerRequestConfig) {
        this.httpMessageVerifier = httpMessageVerifier;
        this.lollipopConsumerRequestConfig = lollipopConsumerRequestConfig;
    }

    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest lollipopConsumerRequest)
            throws LollipopDigestException, UnsupportedEncodingException, LollipopVerifierException {

        Map<String, String> headerParams = lollipopConsumerRequest.getHeaderParams();

        String signature =
                headerParams.get(lollipopConsumerRequestConfig.getSignatureHeader());

        if (signature == null) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE,
                    "Missing Signature Header");
        }

        String signatureInput =
                headerParams.get(lollipopConsumerRequestConfig.getSignatureInputHeader());

        if (signatureInput == null) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE_INPUT,
                    "Missing Signature Input Header");
        }

        if (signatureInput.contains(lollipopConsumerRequestConfig.getContentDigestHeader())) {

            String contentDigest =
                    headerParams.get(lollipopConsumerRequestConfig.getContentDigestHeader());

            String requestBody = lollipopConsumerRequest.getRequestBody();

            String contentEncoding =
                    headerParams.get(lollipopConsumerRequestConfig.getContentEncodingHeader());

            verifyContentDigest(contentDigest, requestBody, contentEncoding);
        }

        return verifyHttpSignature(lollipopConsumerRequest);

    }

    protected boolean verifyContentDigest(
            String contentDigest, String requestBody, String contentEncoding)
            throws LollipopDigestException, UnsupportedEncodingException {

        if (contentDigest == null) {
            throw new LollipopDigestException(
                    LollipopDigestException.ErrorCode.MISSING_DIGEST,
                    "Missing required Content-Digest for validation");
        }

        if (requestBody == null) {
            throw new LollipopDigestException(LollipopDigestException.ErrorCode.MISSING_PAYLOAD,
                    "Missing required payload for digest validation");
        }

        // Attempt to execute digest validation
        if (!httpMessageVerifier.verifyDigest(contentDigest, requestBody, contentEncoding)) {
            throw new LollipopDigestException(
                    LollipopDigestException.ErrorCode.INCORRECT_DIGEST, "Invalid Digest");
        }

        return true;
    }

    /** TODO: stub */
    private boolean verifyHttpSignature(LollipopConsumerRequest lollipopConsumerRequest) {
        return true;
    }
}

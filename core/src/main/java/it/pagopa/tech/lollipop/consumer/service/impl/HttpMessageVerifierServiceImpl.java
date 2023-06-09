/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopSignatureException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;

/** Standard implementation of {@link HttpMessageVerifierService} */
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

    /**
     * @see HttpMessageVerifierService#verifyHttpMessage(LollipopConsumerRequest)
     */
    @Override
    public boolean verifyHttpMessage(LollipopConsumerRequest lollipopConsumerRequest)
            throws LollipopDigestException, UnsupportedEncodingException, LollipopVerifierException,
                    LollipopSignatureException {

        Map<String, String> headerParams = lollipopConsumerRequest.getHeaderParams();

        String signature = headerParams.get(lollipopConsumerRequestConfig.getSignatureHeader());

        if (signature == null) {
            throw new LollipopVerifierException(
                    LollipopVerifierException.ErrorCode.MISSING_SIGNATURE,
                    "Missing Signature Header");
        }

        String signatureInput =
                headerParams.get(lollipopConsumerRequestConfig.getSignatureInputHeader());

        if (signatureInput == null) {
            throw new LollipopVerifierException(
                    LollipopVerifierException.ErrorCode.MISSING_SIGNATURE_INPUT,
                    "Missing Signature-Input Header");
        }

        if (lollipopConsumerRequestConfig.isStrictDigestVerify()
                || hasDigestInSignatureInput(signatureInput)) {

            String contentDigest =
                    headerParams.get(lollipopConsumerRequestConfig.getContentDigestHeader());

            String requestBody = lollipopConsumerRequest.getRequestBody();

            String contentEncoding =
                    headerParams.get(lollipopConsumerRequestConfig.getContentEncodingHeader());

            if (!verifyContentDigest(contentDigest, requestBody, contentEncoding)) {
                throw new LollipopDigestException(
                        LollipopDigestException.ErrorCode.INCORRECT_DIGEST,
                        "Content-Digest does not match the request payload");
            }
        }

        return verifyHttpSignature(signature, signatureInput, headerParams);
    }

    /**
     * Checks if any of the signatures have the content-digest param within the signature input, to
     * determine if in non-strict mode the digest should be validated
     *
     * @param signatureInput request Signature-Input content
     * @return flag to determine if the content-digest is present
     */
    private boolean hasDigestInSignatureInput(String signatureInput) {
        return Arrays.stream(signatureInput.split(";"))
                .anyMatch(
                        signaturePart ->
                                signaturePart.contains("=")
                                        && signaturePart
                                                .toLowerCase(Locale.ROOT)
                                                .contains(
                                                        lollipopConsumerRequestConfig
                                                                .getContentDigestHeader()
                                                                .toLowerCase(Locale.ROOT)));
    }

    /**
     * Checks for required params to be used within the digest validation process, and executes the
     * validation through the usage of the provided implementation of the {@link
     * HttpMessageVerifier}
     *
     * @param contentDigest content digest provided by the request to check
     * @param requestBody payload to be used for digest verification
     * @param contentEncoding encoding for the related payload, if necessary
     * @return flag to determine if the content digest matches the calculated one with the provided
     *     request Body
     * @throws LollipopDigestException exceptions related to the digest validation process
     * @throws UnsupportedEncodingException exceptions related to encoding errors
     */
    private boolean verifyContentDigest(
            String contentDigest, String requestBody, String contentEncoding)
            throws LollipopDigestException, UnsupportedEncodingException {

        if (contentDigest == null) {
            throw new LollipopDigestException(
                    LollipopDigestException.ErrorCode.MISSING_DIGEST,
                    "Missing required Content-Digest for validation");
        }

        if (requestBody == null) {
            throw new LollipopDigestException(
                    LollipopDigestException.ErrorCode.MISSING_PAYLOAD,
                    "Missing required payload for digest validation");
        }

        // Attempt to execute digest validation
        return httpMessageVerifier.verifyDigest(contentDigest, requestBody, contentEncoding);
    }

    /**
     * @param signature
     * @param signatureInput
     * @param headerParams
     * @return
     * @throws LollipopSignatureException
     */
    private boolean verifyHttpSignature(
            String signature, String signatureInput, Map<String, String> headerParams)
            throws LollipopSignatureException {

        HashMap<String, String> headersToProcess = new HashMap<>(headerParams);
        headersToProcess.remove(lollipopConsumerRequestConfig.getSignatureHeader());
        headersToProcess.remove(lollipopConsumerRequestConfig.getSignatureInputHeader());
        return httpMessageVerifier.verifyHttpSignature(signature, signatureInput, headersToProcess);
    }
}

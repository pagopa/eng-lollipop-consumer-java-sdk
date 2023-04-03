package it.pagopa.lollipop.consumer.http.verifier.visma;

import it.pagopa.common.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.common.lollipop.consumer.http_verifier.HttpMessageVerifier;
import net.visma.autopay.http.digest.DigestException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
    Implementation of the @HttpMessageVerifier using Visma-AutoPay http-signature of the http-signature draft
 */
public class VismaHttpMessageVerifier implements HttpMessageVerifier {

    /**
     * Validates digest using Visma DigestVerifier method. Applies contentEncoding if present, otherwise defaults
     * to UTF-8
     *
     * @param digest Request digest
     * @param requestBody Request body
     * @param encoding Content encoding
     * @return boolean with value true if digest validated
     * @throws LollipopDigestException if error from DigestVerifier
     * @throws UnsupportedEncodingException if attempted to encode with an unsupported format
     */
    @Override
    public boolean verifyDigest(String digest, String requestBody, String encoding)
            throws LollipopDigestException, UnsupportedEncodingException {
        try {
            net.visma.autopay.http.digest.DigestVerifier.verifyDigestHeader(digest,
                    requestBody.getBytes(encoding != null ?
                            encoding : "UTF-8"));
            return true;
        } catch (DigestException e) {
            throw new LollipopDigestException(
                    ErrorCodeConverter.convertErrorCode(e.getErrorCode()),
                    e.getMessage(),
                    e);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /*

     */
    @Override
    public boolean verifyHttpSignature(String signature, String signatureInput, Map<String, String> parameters) {
        return false;
    }

}

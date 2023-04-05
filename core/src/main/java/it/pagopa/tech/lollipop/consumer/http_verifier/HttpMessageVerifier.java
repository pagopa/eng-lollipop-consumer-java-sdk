/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier;

import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Interface of the Http Verifier. This interface provides services through an implementation of the
 * http-signature draft
 */
public interface HttpMessageVerifier {

    /**
     * Checks whether the calculated digest of the payload matches the content-digest of the http
     * request
     *
     * @param digest Content-Digest for the request payload
     * @param requestBody Request payload
     * @param encoding Encoding to be used for the payload. To be used if the underlying
     *     implementation checks uses the content byte array
     * @return boolean set to true if the digest check is valid
     * @throws LollipopDigestException
     * @throws UnsupportedEncodingException
     */
    boolean verifyDigest(String digest, String requestBody, String encoding)
            throws LollipopDigestException, UnsupportedEncodingException;

    /**
     * Checks whether the calculated signatures of the required parameter matches with those
     * provided within the Signature param
     *
     * @param signature
     * @param signatureInput
     * @param parameters
     * @return boolean set to true if the signature check is valid
     */
    boolean verifyHttpSignature(
            String signature, String signatureInput, Map<String, String> parameters);
}

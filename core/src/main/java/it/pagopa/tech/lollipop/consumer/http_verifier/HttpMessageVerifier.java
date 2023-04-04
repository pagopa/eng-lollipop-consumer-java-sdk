package it.pagopa.tech.lollipop.consumer.http_verifier;

import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface HttpMessageVerifier {

    boolean verifyDigest(String digest, String requestBody, String encoding) throws LollipopDigestException, UnsupportedEncodingException;
    boolean verifyHttpSignature(String signature, String signatureInput, Map<String, String> parameters);

}

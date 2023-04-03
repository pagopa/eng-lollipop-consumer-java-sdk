package it.pagopa.spid.common.lc.http_verifier;

import java.util.Map;

public interface HttpMessageVerifier {

    boolean verifyDigest(String digest, String requestBody);
    boolean verifyHttpSignature(String signature, String signatureInput, Map<String, String> parameters);

}

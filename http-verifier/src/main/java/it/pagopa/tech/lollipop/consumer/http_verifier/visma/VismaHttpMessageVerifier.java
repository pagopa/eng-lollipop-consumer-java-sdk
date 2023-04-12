/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopSignatureException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import lombok.AllArgsConstructor;
import net.visma.autopay.http.digest.DigestException;
import net.visma.autopay.http.signature.*;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the @HttpMessageVerifier using Visma-AutoPay http-signature of the
 * http-signature draft
 */
@AllArgsConstructor
public class VismaHttpMessageVerifier implements HttpMessageVerifier {

    String defaultEncoding;
    LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    /**
     * Validates digest using Visma DigestVerifier method. Applies contentEncoding if present,
     * otherwise defaults to UTF-8
     *
     * @param digest Request digest
     * @param requestBody Request body
     * @param encoding Content encoding, if missing uses defaultEncoding
     * @return boolean with value true if digest validated
     * @throws LollipopDigestException if error from DigestVerifier
     * @throws UnsupportedEncodingException if attempted to encode with an unsupported format
     */
    @Override
    public boolean verifyDigest(String digest, String requestBody, String encoding)
            throws LollipopDigestException, UnsupportedEncodingException {
        try {
            net.visma.autopay.http.digest.DigestVerifier.verifyDigestHeader(
                    digest, requestBody.getBytes(encoding != null ? encoding :
                            defaultEncoding));
            return true;
        } catch (DigestException e) {
            throw new LollipopDigestException(
                    ErrorCodeConverter.convertErrorCode(e.getErrorCode()), e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /** TODO: stub */
    @Override
    public boolean verifyHttpSignature(
            String signature, String signatureInput, Map<String, String> parameters) throws LollipopSignatureException {

        String lollipopKey = parameters.get(lollipopConsumerRequestConfig.getLollipopKeyHeader());
        if (lollipopKey == null) {
            throw new LollipopSignatureException(LollipopSignatureException.ErrorCode.MISSING_PUBLIC_KEY,
                    "Could not find the public key within the expected header");
        }

        String[] signatures = signature.split(",");
        String[] signatureInputs = signatureInput.split(",");

        if (signatures.length != signatureInputs.length) {
            throw new LollipopSignatureException(LollipopSignatureException.ErrorCode.INVALID_SIGNATURE_NUMBER,
                    "Available signatures and signature-inputs differ in number");
        }

        for (int i= 0; i < signatures.length; i++) {
            String signatureToProcess = signatures[i];
            String signatureInputToProcess = signatureInputs[i];

            String label = signatureToProcess.split("=")[0];
            SignatureAlgorithm signatureAlgorithm = null;

            /* Extract algorithm from signature input*/
            Pattern pattern = Pattern.compile(";alg=(.*?);'");
            Matcher matcher = pattern.matcher(signatureInputToProcess);
            if (matcher.find())
            {
                try {

                    String algToUse = matcher.group(0);
                    signatureAlgorithm = SignatureAlgorithm.valueOf(algToUse);

                } catch (IndexOutOfBoundsException | IllegalStateException e) {
                    throw new LollipopSignatureException(LollipopSignatureException.ErrorCode
                            .INVALID_SIGNATURE_ALG, "Algorithm required not available", e);
                }
            }

            var signatureContext =
                    SignatureContext.builder()
                            .headers(parameters)
                            .build();

            PublicKey publicKey = null;
            try {
                JWK jwk = JWK.parse(new String(Base64.getDecoder().decode(lollipopKey)));
                KeyType keyType = jwk.getKeyType();
                if (KeyType.EC.equals(keyType)) {
                    publicKey = jwk.toECKey().toPublicKey();
                } else if (KeyType.RSA.equals(keyType)) {
                    publicKey = jwk.toRSAKey().toPublicKey();
                }
            } catch (ParseException | JOSEException e) {
                throw new RuntimeException(e);
            }

            SignatureAlgorithm finalSignatureAlgorithm = signatureAlgorithm;
            PublicKey finalPublicKey = publicKey;
            VerificationSpec verificationSpec = VerificationSpec
                    .builder()
                    .signatureLabel(label)
                    .context(signatureContext)
                    .publicKeyGetter(keyId -> PublicKeyInfo.builder()
                            .publicKey(finalPublicKey)
                            .algorithm(finalSignatureAlgorithm)
                            .build()
                    )
                    .build();

            try {
                verificationSpec.verify();
            } catch (SignatureException e) {
                throw new LollipopSignatureException(LollipopSignatureException.ErrorCode.INVALID_SIGNATURE,
                        "The provided signature is invalid", e);
            }

        }

        return true;
    }

}

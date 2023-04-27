/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopSignatureException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.visma.autopay.http.digest.DigestException;
import net.visma.autopay.http.signature.*;
import net.visma.autopay.http.structured.StructuredBytes;

/**
 * Implementation of the @HttpMessageVerifier using Visma-AutoPay http-signature of the
 * http-signature draft
 */
@AllArgsConstructor
@Slf4j
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
                    digest, requestBody.getBytes(encoding != null ? encoding : defaultEncoding));
            return true;
        } catch (DigestException e) {
            throw new LollipopDigestException(
                    ErrorCodeConverter.convertErrorCode(e.getErrorCode()), e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /**
     * @param signature Input parameter containing the expected signature
     * @param signatureInput Input parameter containing the expected signature base for validation
     * @param parameters Header parameters to be used in signature validation
     * @return boolean to determine if validated
     * @throws LollipopSignatureException exception containing all the error codes related to
     *     signature validation
     */
    @Override
    public boolean verifyHttpSignature(
            String signature, String signatureInput, Map<String, String> parameters)
            throws LollipopSignatureException {

        /* Removed to enable multiple signature validation (if necessary) */
        parameters.remove(lollipopConsumerRequestConfig.getSignatureInputHeader());
        parameters.remove(lollipopConsumerRequestConfig.getSignatureHeader());

        String lollipopKey = parameters.get(lollipopConsumerRequestConfig.getPublicKeyHeader());
        isLollipopKeyNotNull(lollipopKey);

        String[] signatures = signature.split(",");
        String[] signatureInputs = signatureInput.split(",");

        verifySignatureLength(signatures, signatureInputs);

        /* cicle through all signatures to validate */
        for (int i = 0; i < signatures.length; i++) {
            String signatureToProcess = signatures[i];
            String signatureInputToProcess = signatureInputs[i];

            String label = signatureToProcess.split("=")[0];
            SignatureAlgorithm signatureAlgorithm = null;

            /* Extract algorithm from signature input*/
            Pattern pattern = Pattern.compile("alg=\"(.*?)\"");
            Matcher matcher = pattern.matcher(signatureInputToProcess);
            if (matcher.find()) {
                try {

                    String algToUse = matcher.group(0);
                    signatureAlgorithm =
                            SignatureAlgorithm.fromIdentifier(
                                    algToUse.replace("\"", "").split("=")[1]);

                    isSignatureAlgorithmNotNull(signatureAlgorithm);

                } catch (IndexOutOfBoundsException | IllegalStateException e) {
                    throw new LollipopSignatureException(
                            LollipopSignatureException.ErrorCode.INVALID_SIGNATURE_ALG,
                            "Algorithm required not available",
                            e);
                }
            }

            /* Attempt to recover a valid key from the provided jwt */
            PublicKey publicKey = null;
            try {
                JWK jwk = JWK.parse(new String(Base64.getDecoder().decode(lollipopKey)));
                KeyType keyType = jwk.getKeyType();
                if (KeyType.EC.equals(keyType)) {
                    publicKey = jwk.toECKey().toECPublicKey();
                    signatureToProcess = transcodeSignature(signatureToProcess);
                } else if (KeyType.RSA.equals(keyType)) {
                    publicKey = jwk.toRSAKey().toRSAPublicKey();
                }
            } catch (ParseException | JOSEException e) {
                throw new LollipopSignatureException(
                        LollipopSignatureException.ErrorCode.INVALID_SIGNATURE_ALG,
                        "Missing Signature Algorithm");
            }

            var signatureContext =
                    SignatureContext.builder()
                            .headers(parameters)
                            .header("Signature-Input", signatureInputToProcess)
                            .header("Signature", signatureToProcess)
                            .build();

            /* Populate Visma Sign Validator*/
            SignatureAlgorithm finalSignatureAlgorithm = signatureAlgorithm;
            PublicKey finalPublicKey = publicKey;
            VerificationSpec verificationSpec =
                    VerificationSpec.builder()
                            .signatureLabel(label.trim())
                            .context(signatureContext)
                            .publicKeyGetter(
                                    keyId ->
                                            PublicKeyInfo.builder()
                                                    .publicKey(finalPublicKey)
                                                    .algorithm(finalSignatureAlgorithm)
                                                    .build())
                            .build();

            try {
                verificationSpec.verify();
            } catch (SignatureException e) {
                throw new LollipopSignatureException(
                        LollipopSignatureException.ErrorCode.INVALID_SIGNATURE,
                        "The provided signature is invalid",
                        e);
            }
        }

        return true;
    }

    private String transcodeSignature(String signatureToProcess) {
        try {
            String[] signatureParts = signatureToProcess.split("=", 2);
            String signatureValue =
                    StructuredBytes.of(
                                    ECDSA.transcodeSignatureToConcat(
                                            Base64.getMimeDecoder()
                                                    .decode(signatureParts[1].getBytes()),
                                            ECDSA.getSignatureByteArrayLength(JWSAlgorithm.ES256)))
                            .toString();
            ECDSA.ensureLegalSignature(
                    Base64.getMimeDecoder().decode(signatureValue.getBytes()), JWSAlgorithm.ES256);
            signatureToProcess = signatureParts[0].concat("=").concat(signatureValue);
        } catch (Exception e) {
            log.debug("Could not convert EC signature to valid format");
        }
        return signatureToProcess;
    }    

    private static void isSignatureAlgorithmNotNull(SignatureAlgorithm signatureAlgorithm)
            throws LollipopSignatureException {
        if (signatureAlgorithm == null) {
            throw new LollipopSignatureException(
                    LollipopSignatureException.ErrorCode.INVALID_SIGNATURE_ALG,
                    "Missing Signature Algorithm");
        }
    }

    private static void isLollipopKeyNotNull(String lollipopKey) throws LollipopSignatureException {
        if (lollipopKey == null) {
            throw new LollipopSignatureException(
                    LollipopSignatureException.ErrorCode.MISSING_PUBLIC_KEY,
                    "Could not find the public key within the expected header");
        }
    }

    private static void verifySignatureLength(String[] signatures, String[] signatureInputs)
            throws LollipopSignatureException {
        if (signatures.length != signatureInputs.length) {
            throw new LollipopSignatureException(
                    LollipopSignatureException.ErrorCode.INVALID_SIGNATURE_NUMBER,
                    "Available signatures and signature-inputs differ in number");
        }
    }

    private static PublicKey getPublicKey(JWK jwk, KeyType keyType) throws JOSEException {
        PublicKey publicKey = null;
        if (KeyType.EC.equals(keyType)) {
            publicKey = jwk.toECKey().toECPublicKey();
        } else if (KeyType.RSA.equals(keyType)) {
            publicKey = jwk.toRSAKey().toRSAPublicKey();
        }
        return publicKey;
    }
}

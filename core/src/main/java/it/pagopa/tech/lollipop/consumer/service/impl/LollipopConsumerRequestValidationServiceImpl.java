/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionRefAlgorithms;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionType;
import it.pagopa.tech.lollipop.consumer.enumeration.LollipopRequestMethod;
import it.pagopa.tech.lollipop.consumer.exception.LollipopRequestContentValidationException;
import it.pagopa.tech.lollipop.consumer.model.ECPublicKey;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.RSAPublicKey;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import lombok.extern.java.Log;

@Log
public class LollipopConsumerRequestValidationServiceImpl
        implements LollipopConsumerRequestValidationService {

    private final LollipopConsumerRequestConfig config;

    public LollipopConsumerRequestValidationServiceImpl(LollipopConsumerRequestConfig config) {
        this.config = config;
    }

    @Override
    public void validateLollipopRequest(LollipopConsumerRequest request)
            throws LollipopRequestContentValidationException {
        Map<String, String> headerParams = request.getHeaderParams();

        validatePublicKey(headerParams.get(this.config.getPublicKeyHeader()));
        validateAssertionRefHeader(headerParams.get(this.config.getAssertionRefHeader()));
        validateAssertionTypeHeader(headerParams.get(this.config.getAssertionTypeHeader()));
        validateUserIdHeader(headerParams.get(this.config.getUserIdHeader()));
        validateAuthJWTHeader(headerParams.get(this.config.getAuthJWTHeader()));
        validateOriginalMethodHeader(headerParams.get(this.config.getOriginalMethodHeader()));
        validateOriginalURLHeader(headerParams.get(this.config.getOriginalURLHeader()));
        validateSignatureInputHeader(headerParams.get(this.config.getSignatureInputHeader()));
        validateSignatureHeader(headerParams.get(this.config.getSignatureHeader()));
    }

    private void validatePublicKey(String publicKey)
            throws LollipopRequestContentValidationException {
        if (publicKey == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_PUBLIC_KEY,
                    "Missing Public Key Header");
        }

        try {
            publicKey = new String(Base64.getDecoder().decode(publicKey.getBytes()));
        } catch (Exception e) {
            log.log(Level.FINE, "Key not in Base64");
        }

        if (isNotValidPublicKey(publicKey, ECPublicKey.class)
                && isNotValidPublicKey(publicKey, RSAPublicKey.class)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_PUBLIC_KEY,
                    "Invalid Public Key Header value");
        }
    }

    private boolean isNotValidPublicKey(String publicKey, Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(publicKey, clazz);
        } catch (JsonProcessingException e) {
            return true;
        }
        return false;
    }

    private void validateAssertionRefHeader(String assertionRef)
            throws LollipopRequestContentValidationException {
        if (assertionRef == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_ASSERTION_REF,
                    "Missing AssertionRef Header");
        }

        if (isNotValidAssertionRef(assertionRef)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_ASSERTION_REF,
                    "Invalid AssertionRef Header value");
        }
    }

    private boolean isNotValidAssertionRef(String signature) {
        boolean matchesSHA256 =
                AssertionRefAlgorithms.SHA256.getPattern().matcher(signature).matches();
        boolean matchesSHA384 =
                AssertionRefAlgorithms.SHA384.getPattern().matcher(signature).matches();
        boolean matchesSHA512 =
                AssertionRefAlgorithms.SHA512.getPattern().matcher(signature).matches();
        return !matchesSHA256 && !matchesSHA384 && !matchesSHA512;
    }

    private void validateAssertionTypeHeader(String assertionType)
            throws LollipopRequestContentValidationException {
        if (assertionType == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_ASSERTION_TYPE,
                    "Missing Assertion Type Header");
        }

        if ((!isAssertionTypeSupported(assertionType))) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_ASSERTION_TYPE,
                    "Invalid Assertion Type Header value, type not supported");
        }
    }

    private boolean isAssertionTypeSupported(String assertionType) {
        for (AssertionType supportedType : AssertionType.values()) {
            if (supportedType.name().equals(assertionType)) {
                return true;
            }
        }
        return false;
    }

    private void validateUserIdHeader(String userId)
            throws LollipopRequestContentValidationException {
        if (userId == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_USER_ID,
                    "Missing User Id Header");
        }

        if ((isNotValidFiscalCode(userId))) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_USER_ID,
                    "Invalid User Id Header value, type not supported");
        }
    }

    private boolean isNotValidFiscalCode(String userId) {
        return !Pattern.compile(
                        "^[A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$")
                .matcher(userId)
                .matches();
    }

    private void validateAuthJWTHeader(String authJWT)
            throws LollipopRequestContentValidationException {
        if (authJWT == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_AUTH_JWT,
                    "Missing Auth JWT Header");
        }

        if (authJWT.isBlank()) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_AUTH_JWT,
                    "Invalid Auth JWT Header value, cannot be empty");
        }
    }

    private void validateOriginalMethodHeader(String originalMethod)
            throws LollipopRequestContentValidationException {
        if (originalMethod == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_ORIGINAL_METHOD,
                    "Missing Original Method Header");
        }

        if (!isRequestMethodSupported(originalMethod)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_ORIGINAL_METHOD,
                    "Invalid Original Method Header value, method not supported");
        }

        if (!originalMethod.equals(this.config.getExpectedFirstLcOriginalMethod())) {
            String errMsg = String.format("Unexpected original method: %s", originalMethod);
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.UNEXPECTED_ORIGINAL_METHOD,
                    errMsg);
        }
    }

    private boolean isRequestMethodSupported(String originalMethod) {
        for (LollipopRequestMethod supportedType : LollipopRequestMethod.values()) {
            if (supportedType.name().equals(originalMethod)) {
                return true;
            }
        }
        return false;
    }

    private void validateOriginalURLHeader(String originalURL)
            throws LollipopRequestContentValidationException {
        if (originalURL == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_ORIGINAL_URL,
                    "Missing Original URL Header");
        }

        if (isNotValidOriginalURL(originalURL)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_ORIGINAL_URL,
                    "Invalid Original URL Header value");
        }

        if (!originalURL.equals(this.config.getExpectedFirstLcOriginalUrl())) {
            String errMsg = String.format("Unexpected original url: %s", originalURL);
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.UNEXPECTED_ORIGINAL_URL,
                    errMsg);
        }
    }

    private boolean isNotValidOriginalURL(String originalURL) {
        return !Pattern.compile("^https://\\S+").matcher(originalURL).matches();
    }

    private void validateSignatureInputHeader(String signatureInput)
            throws LollipopRequestContentValidationException {
        if (signatureInput == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_SIGNATURE_INPUT,
                    "Missing Signature Input Header");
        }

        if (isNotValidSignatureInput(signatureInput)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_SIGNATURE_INPUT,
                    "Invalid Signature Input Header value");
        }
    }

    private boolean isNotValidSignatureInput(String signatureInput) {
        return !Pattern.compile("^(((sig[\\d]++)=[^,]*+)(, ?+)?+)++$")
                .matcher(signatureInput)
                .matches();
    }

    private void validateSignatureHeader(String signature)
            throws LollipopRequestContentValidationException {
        if (signature == null) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.MISSING_SIGNATURE,
                    "Missing Signature Header");
        }

        if (isNotValidSignature(signature)) {
            throw new LollipopRequestContentValidationException(
                    LollipopRequestContentValidationException.ErrorCode.INVALID_SIGNATURE,
                    "Invalid Signature Header value");
        }
    }

    private boolean isNotValidSignature(String signature) {
        return !Pattern.compile("^((sig[\\d]+)=:[A-Za-z0-9+/=]*+:(, ?)?)+$")
                .matcher(signature)
                .matches();
    }
}

package it.pagopa.tech.lollipop.consumer.config.service;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionTypeEnum;
import it.pagopa.tech.lollipop.consumer.enumeration.LollipopMethodEnum;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

import java.util.Map;
import java.util.regex.Pattern;

public class LollipopConsumerRequestValidationService {

    public static boolean validateLollipopConsumerConfig(LollipopConsumerRequest request, LollipopConsumerRequestConfig config) throws LollipopVerifierException {

        Map<String, String> headerParams = request.getHeaderParams();

        validatePublicKey(headerParams.get(config.getPublicKeyHeader()));

        validateAssertionRefHeader(headerParams.get(config.getAssertionRefHeader()));
        validateAssertionTypeHeader(headerParams.get(config.getAssertionTypeHeader()));
        validateUserIdHeader(headerParams.get(config.getUserIdHeader()));
        validateAuthJWTHeader(headerParams.get(config.getAuthJWTHeader()));
        validateOriginalMethodHeader(headerParams.get(config.getOriginalMethodHeader()));
        validateOriginalURLHeader(headerParams.get(config.getOriginalURLHeader()));
        validateSignatureInputHeader(headerParams.get(config.getSignatureInputHeader()));
        validateSignatureHeader(headerParams.get(config.getSignatureHeader()));

        return true;
    }

    private static void validatePublicKey(String publicKey) throws LollipopVerifierException {
        if (publicKey == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_PUBLIC_KEY, "Missing Public Key Header");
        }

        if (isNotValidPublicKey(publicKey)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_PUBLIC_KEY, "Invalid Public Key Header value");
        }
    }

    private static boolean isNotValidPublicKey(String publicKey) {
        // TODO
        return false;
    }

    private static void validateAssertionRefHeader(String assertionRef) throws LollipopVerifierException {
        if (assertionRef == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_ASSERTION_REF, "Missing AssertionRef Header");
        }

        if (isNotValidAssertionRef(assertionRef)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_ASSERTION_REF, "Invalid AssertionRef Header value");
        }
    }

    private static boolean isNotValidAssertionRef(String signature) {
        boolean matchesSHA26 = Pattern.compile("^(sha256-[A-Za-z0-9-_=]{1,44})$").matcher(signature).matches();
        boolean matchesSHA384 = Pattern.compile("^(sha384-[A-Za-z0-9-_=]{1,66})$").matcher(signature).matches();
        boolean matchesSHA512 = Pattern.compile("^(sha512-[A-Za-z0-9-_=]{1,88})$").matcher(signature).matches();
        return !matchesSHA26 && !matchesSHA384 && !matchesSHA512;
    }

    private static void validateAssertionTypeHeader(String assertionType) throws LollipopVerifierException {
        if (assertionType == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_ASSERTION_TYPE, "Missing Assertion Type Header");
        }

        if ((!isAssertionTypeSupported(assertionType))) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_ASSERTION_TYPE, "Invalid Assertion Type Header value, type not supported");
        }
    }

    public static boolean isAssertionTypeSupported(String assertionType) {
        for (AssertionTypeEnum supportedType : AssertionTypeEnum.values()) {
            if (supportedType.name().equals(assertionType)) {
                return true;
            }
        }
        return false;
    }

    private static void validateUserIdHeader(String userId) throws LollipopVerifierException {
        if (userId == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_USER_ID, "Missing User Id Header");
        }

        if ((isNotValidFiscalCode(userId))) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_USER_ID, "Invalid User Id Header value, type not supported");
        }
    }

    private static boolean isNotValidFiscalCode(String userId) {
        return !Pattern.compile("^[A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$").matcher(userId).matches();
    }

    private static void validateAuthJWTHeader(String authJWT) throws LollipopVerifierException {
        if (authJWT == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_AUTH_JWT, "Missing Auth JWT Header");
        }

        if (authJWT.isBlank()) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_AUTH_JWT, "Invalid Auth JWT Header value, cannot be empty");
        }
    }

    private static void validateOriginalMethodHeader(String originalMethod) throws LollipopVerifierException {
        if (originalMethod == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_ORIGINAL_METHOD, "Missing Original Method Header");
        }

        if (!isRequestMethodSupported(originalMethod)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_ORIGINAL_METHOD, "Invalid Original Method Header value, method not supported");
        }
    }

    public static boolean isRequestMethodSupported(String originalMethod) {
        for (LollipopMethodEnum supportedType : LollipopMethodEnum.values()) {
            if (supportedType.name().equals(originalMethod)) {
                return true;
            }
        }
        return false;
    }

    private static void validateOriginalURLHeader(String originalURL) throws LollipopVerifierException {
        if (originalURL == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_ORIGINAL_URL, "Missing Original URL Header");
        }

        if (isNotValidOriginalURL(originalURL)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_ORIGINAL_URL, "Invalid Original URL Header value");
        }
    }

    private static boolean isNotValidOriginalURL(String originalURL) {
        return !Pattern.compile("^https://").matcher(originalURL).matches();
    }

    private static void validateSignatureInputHeader(String signatureInput) throws LollipopVerifierException {
        if (signatureInput == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE_INPUT, "Missing Signature Input Header");
        }

        if (isNotValidSignatureInput(signatureInput)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_ORIGINAL_URL, "Invalid Signature Input Header value");
        }
    }

    private static boolean isNotValidSignatureInput(String signatureInput) {
        return !Pattern.compile("^(((sig[0-9]+)=[^,]*?)(, ?)?)+$").matcher(signatureInput).matches();
    }

    private static void validateSignatureHeader(String signature) throws LollipopVerifierException {
        if (signature == null ) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE, "Missing Signature Header");
        }

        if (isNotValidSignature(signature)) {
            throw new LollipopVerifierException(LollipopVerifierException.ErrorCode.INVALID_SIGNATURE_HEADER, "Invalid Signature Header value");
        }
    }

    private static boolean isNotValidSignature(String signature) {
        return !Pattern.compile("^((sig[0-9]+)=:[A-Za-z0-9+/=]*:(, ?)?)+$").matcher(signature).matches();
    }
}

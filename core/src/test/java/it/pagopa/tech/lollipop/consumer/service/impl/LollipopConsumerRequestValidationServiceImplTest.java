/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionType;
import it.pagopa.tech.lollipop.consumer.exception.LollipopRequestContentValidationException;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LollipopConsumerRequestValidationServiceImplTest {

    private LollipopConsumerRequestConfig config;
    private LollipopConsumerRequestValidationService sut;

    private final Random random = new Random();

    public static final String VALID_EC_PUBLIC_KEY =
            "{  \"kty\": \"EC\",  \"x\": \"FqFDuwEgu4MUXERPMVL-85pGv2D3YmL4J1gfMkdbc24\",  \"y\":"
                    + " \"hdV0oxmWFSxMoJUDpdihr76rS8VRBEqMFebYyAfK9-k\",  \"crv\": \"P-256\"}";
    public static final String VALID_RSA_PUBLIC_KEY =
            "{  \"alg\": \"RS256\",  \"e\": \"AQAB\",  \"kty\": \"RSA\",  \"n\":"
                + " \"yeNlzlub94YgerT030codqEztjfU_S6X4DbDA_iVKkjAWtYfPHDzz_sPCT1Axz6isZdf3lHpq_gYX4Sz\"}";
    public static final String VALID_ASSERTION_REF =
            "sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg";
    public static final String VALID_FISCAL_CODE = "AAAAAA89S20I111X";
    public static final String VALID_JWT = "aValidJWT";
    public static final String VALID_SIGNATURE_INPUT =
            "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
    public static final String VALID_SIGNATURE =
            "sig1=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";

    @BeforeEach
    void setUp() {
        config = spy(LollipopConsumerRequestConfig.builder().build());
        sut = new LollipopConsumerRequestValidationServiceImpl(config);
    }

    @Test
    void validatePublicKeyFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_PUBLIC_KEY,
                e.getErrorCode());
    }

    @Test
    void validatePublicKeyFailureHeaderInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_PUBLIC_KEY,
                e.getErrorCode());
    }

    @Test
    void validateAssertionRefFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_ASSERTION_REF,
                e.getErrorCode());
    }

    @Test
    void validateAssertionRefFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_ASSERTION_REF,
                e.getErrorCode());
    }

    @Test
    void validateAssertionTypeFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_ASSERTION_TYPE,
                e.getErrorCode());
    }

    @Test
    void validateAssertionTypeFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_ASSERTION_TYPE,
                e.getErrorCode());
    }

    @Test
    void validateUserIdFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_USER_ID,
                e.getErrorCode());
    }

    @Test
    void validateUserIdFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_USER_ID,
                e.getErrorCode());
    }

    @Test
    void validateAuthJWKFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_AUTH_JWT,
                e.getErrorCode());
    }

    @Test
    void validateAuthJWKFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), "");
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_AUTH_JWT,
                e.getErrorCode());
    }

    @Test
    void validateOriginalMethodFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_ORIGINAL_METHOD,
                e.getErrorCode());
    }

    @Test
    void validateOriginalMethodFailureNotSupported() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), "INVALID_METHOD");
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_ORIGINAL_METHOD,
                e.getErrorCode());
    }

    @Test
    void validateOriginalMethodFailureDifferentFromExpectedMethod() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), "PUT");
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.UNEXPECTED_ORIGINAL_METHOD,
                e.getErrorCode());
    }

    @Test
    void validateOriginalURLFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_ORIGINAL_URL,
                e.getErrorCode());
    }

    @Test
    void validateOriginalURLFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_ORIGINAL_URL,
                e.getErrorCode());
    }

    @Test
    void validateOriginalURLFailureDifferentFromExpectedMethod() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), "https://pagopa.it");
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.UNEXPECTED_ORIGINAL_URL,
                e.getErrorCode());
    }

    @Test
    void validateSignatureInputFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_SIGNATURE_INPUT,
                e.getErrorCode());
    }

    @Test
    void validateSignatureInputFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        headers.put(config.getSignatureInputHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_SIGNATURE_INPUT,
                e.getErrorCode());
    }

    @Test
    void validateSignatureFailureHeaderNotPresent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        headers.put(config.getSignatureInputHeader(), VALID_SIGNATURE_INPUT);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.MISSING_SIGNATURE,
                e.getErrorCode());
    }

    @Test
    void validateSignatureFailureInvalidFormat() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        headers.put(config.getSignatureInputHeader(), VALID_SIGNATURE_INPUT);
        headers.put(config.getSignatureHeader(), generateRandomString());
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        LollipopRequestContentValidationException e =
                assertThrows(
                        LollipopRequestContentValidationException.class,
                        () -> sut.validateLollipopRequest(request));

        assertEquals(
                LollipopRequestContentValidationException.ErrorCode.INVALID_SIGNATURE,
                e.getErrorCode());
    }

    @Test
    void validateRequestSuccessWithECPublicKey() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        headers.put(config.getSignatureInputHeader(), VALID_SIGNATURE_INPUT);
        headers.put(config.getSignatureHeader(), VALID_SIGNATURE);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        assertDoesNotThrow(() -> sut.validateLollipopRequest(request));
    }

    @Test
    void validateRequestSuccessWithRSAPublicKey() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_RSA_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), config.getExpectedFirstLcOriginalUrl());
        headers.put(config.getSignatureInputHeader(), VALID_SIGNATURE_INPUT);
        headers.put(config.getSignatureHeader(), VALID_SIGNATURE);
        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder().headerParams(headers).build();

        assertDoesNotThrow(() -> sut.validateLollipopRequest(request));
    }

    private String generateRandomString() {
        byte[] array = new byte[7];
        random.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}

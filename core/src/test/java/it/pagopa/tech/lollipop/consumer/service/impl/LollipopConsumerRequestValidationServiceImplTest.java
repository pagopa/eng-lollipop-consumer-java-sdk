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
            "{  \"alg\": \"RS256\",  \"e\": \"65537\",  \"kty\": \"RSA\",  \"n\":"
                + " \"16664736175603627996319962836030881026179675012391119517975514948152431214653585662880486636564539745534321011181408561816254231231298259205135081219875827651147217038442994953270212442857910417611387549687536933145745249602198835932059392377695498325446146715840517338191125529557810596070318285357964276748438650077150378696894010172596714187128214451872453277619054588751139432194135913672107689362828514055714059473608142304229480488308405791341245363647711560656764853819020066812645413910427819478301754525254844345246642430554339909098721902422359723272095429198014557278590405542226255562568066559844209030611\"}";
    public static final String VALID_ASSERTION_REF =
            "sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg";
    public static final String VALID_FISCAL_CODE = "AAAAAA89S20I111X";
    public static final String VALID_JWT = "aValidJWT";
    public static final String VALID_SIGNATURE_INPUT =
            "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
    public static final String VALID_SIGNATURE =
            "sig1=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";
    public static final String VALID_ORIGINAL_URL =
            "https://api-app.io.pagopa.it/first-lollipop/0123ABCD/sign";

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
    void validateOriginalURLFailureWithSameSubstring() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL + "/another-path");
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
    void validateOriginalURLFailureRegexPatter() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(config.getPublicKeyHeader(), VALID_EC_PUBLIC_KEY);
        headers.put(config.getAssertionRefHeader(), VALID_ASSERTION_REF);
        headers.put(config.getAssertionTypeHeader(), AssertionType.SAML.name());
        headers.put(config.getUserIdHeader(), VALID_FISCAL_CODE);
        headers.put(config.getAuthJWTHeader(), VALID_JWT);
        headers.put(config.getOriginalMethodHeader(), config.getExpectedFirstLcOriginalMethod());
        headers.put(
                config.getOriginalURLHeader(),
                "https://api-app.io.pagopa.it/first-lollipop/0123_ABCD*/sign");
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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
        headers.put(config.getOriginalURLHeader(), VALID_ORIGINAL_URL);
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

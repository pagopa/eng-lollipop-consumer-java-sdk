/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import static it.pagopa.tech.lollipop.consumer.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import lombok.SneakyThrows;
import org.apache.geronimo.mail.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

class AssertionVerifierServiceImplTest {

    private IdpCertProvider idpCertProviderMock;
    private AssertionService assertionServiceMock;
    private static LollipopConsumerRequestConfig lollipopRequestConfigMock;

    private AssertionVerifierServiceImpl sut;

    @BeforeEach
    void setUp() {
        idpCertProviderMock = mock(IdpCertProvider.class);
        assertionServiceMock = mock(AssertionService.class);
        lollipopRequestConfigMock = spy(LollipopConsumerRequestConfig.builder().build());

        sut =
                spy(
                        new AssertionVerifierServiceImpl(
                                idpCertProviderMock,
                                assertionServiceMock,
                                lollipopRequestConfigMock));
    }

    @SneakyThrows
    @Test
    void validateLollipopGetAssertionFailureWithOidcAssertionException() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        doThrow(OidcAssertionNotSupported.class)
                .when(assertionServiceMock)
                .getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e =
                assertThrows(
                        ErrorRetrievingAssertionException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertTrue(e.getCause() instanceof OidcAssertionNotSupported);
        Assertions.assertEquals(
                ErrorRetrievingAssertionException.ErrorCode.OIDC_TYPE_NOT_SUPPORTED,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetAssertionFailureWithAssertionNotFoundException() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        doThrow(LollipopAssertionNotFoundException.class)
                .when(assertionServiceMock)
                .getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e =
                assertThrows(
                        ErrorRetrievingAssertionException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertTrue(e.getCause() instanceof LollipopAssertionNotFoundException);
        Assertions.assertEquals(
                ErrorRetrievingAssertionException.ErrorCode.SAML_ASSERTION_NOT_FOUND,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetAssertionFailureForBuildAssertionDocError() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData("");

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e =
                assertThrows(
                        ErrorRetrievingAssertionException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorRetrievingAssertionException.ErrorCode.ERROR_PARSING_ASSERTION,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetAssertionSuccess() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(EMPTY_ASSERTION_XML);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopValidatePeriodFailureWithFieldNotFoundInAssertionDoc() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(EMPTY_ASSERTION_XML);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e =
                assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionPeriodException.ErrorCode.INVALID_ASSERTION_PERIOD, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidatePeriodFailureWithInvalidDateFormat() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_PERIOD_DATE_FORMAT);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e =
                assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionPeriodException.ErrorCode.ERROR_PARSING_ASSERTION_NOT_BEFORE_DATE,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidatePeriodFailureWithExpiredAssertion() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_EXPIRED_PERIOD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e =
                assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionPeriodException.ErrorCode.INVALID_ASSERTION_PERIOD, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidatePeriodSuccess() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(
                new String(Base64.decode(ASSERTION_XML_TIM.getBytes(StandardCharsets.UTF_8))));

        doReturn(365 * 20).when(lollipopRequestConfigMock).getAssertionExpireInDays();

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateUserIdFailureWithoutAttributeTagInAssertionDoc() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_ATTRIBUTE_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));

        AssertionUserIdException e =
                assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateUserIdFailureWithoutFiscalCodeInAssertionDoc() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_FISCAL_CODE);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));

        AssertionUserIdException e =
                assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateUserIdFailureWithInvalidUserIdHeader() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));

        AssertionUserIdException e =
                assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionUserIdException.ErrorCode.INVALID_USER_ID, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateUserIdSuccess() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void
            validateLollipopValidateThumbprintFailureWithoutSubjectConfirmationDataTagInAssertionDoc() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));

        AssertionThumbprintException e =
                assertThrows(
                        AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_FIELD_NOT_FOUND,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateThumbprintFailureWithInvalidInResponseToAlgorithm() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_INRESPONSETO_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));

        AssertionThumbprintException e =
                assertThrows(
                        AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_ALGORITHM_NOT_VALID,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateThumbprintFailureWithErrorCalculatingThumbprint() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));

        AssertionThumbprintException e =
                assertThrows(
                        AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionThumbprintException.ErrorCode.ERROR_CALCULATING_ASSERTION_THUMBPRINT,
                e.getErrorCode());
        Assertions.assertTrue(e.getCause() instanceof ParseException);
    }

    @SneakyThrows
    @Test
    void
            validateLollipopValidateThumbprintFailureWithDifferentAssertionRefAndCalculatedThumbprint() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", VALID_JWK, "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));

        AssertionThumbprintException e =
                assertThrows(
                        AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                AssertionThumbprintException.ErrorCode.INVALID_IN_RESPONSE_TO, e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateThumbprintSHA256Success() {
        LollipopConsumerRequest request =
                getLollipopConsumerRequest(VALID_SHA_256_ASSERTION_REF, VALID_JWK, "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateThumbprintSHA384Success() {
        LollipopConsumerRequest request =
                getLollipopConsumerRequest(VALID_SHA_384_ASSERTION_REF, VALID_JWK, "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA384_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateThumbprintSHA512Success() {
        LollipopConsumerRequest request =
                getLollipopConsumerRequest(VALID_SHA_512_ASSERTION_REF, VALID_JWK, "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA512_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.emptyList()).when(sut).getIdpCertData(any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopGetIdpCertDataFailureForMissingInstantField() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_INSTANT_FIELD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));

        ErrorRetrievingIdpCertDataException e =
                assertThrows(
                        ErrorRetrievingIdpCertDataException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorRetrievingIdpCertDataException.ErrorCode.INSTANT_FIELD_NOT_FOUND,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetIdpCertDataFailureForMissingEntityIdField() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_ENTITY_ID_FIELD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));

        ErrorRetrievingIdpCertDataException e =
                assertThrows(
                        ErrorRetrievingIdpCertDataException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorRetrievingIdpCertDataException.ErrorCode.ENTITY_ID_FIELD_NOT_FOUND,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetIdpCertDataFailureForIdpCertDataNotFoundException() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_ENTITY_ID_FIELD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doThrow(CertDataNotFoundException.class)
                .when(idpCertProviderMock)
                .getIdpCertData(anyString(), anyString());

        ErrorRetrievingIdpCertDataException e =
                assertThrows(
                        ErrorRetrievingIdpCertDataException.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorRetrievingIdpCertDataException.ErrorCode.IDP_CERT_DATA_NOT_FOUND,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopGetIdpCertDataSuccessWithWarningForInvalidInstantDateFormat() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_INSTANT_FORMAT);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopGetIdpCertDataSuccess() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_ENTITY_ID_FIELD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true).when(sut).validateSignature(any(Document.class), anyList());

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateSignatureFailureForErrorUnmarshalAssertion() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(EMPTY_ASSERTION_XML);

        IdpCertData idpCertData = new IdpCertData();
        idpCertData.setCertData(Collections.singletonList(CERTIFICATE_TIM_LATEST));

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.singletonList(idpCertData))
                .when(sut)
                .getIdpCertData(any(Document.class));

        ErrorValidatingAssertionSignature e =
                assertThrows(
                        ErrorValidatingAssertionSignature.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorValidatingAssertionSignature.ErrorCode.ERROR_PARSING_ASSERTION,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateSignatureFailureForMissingSignature() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        IdpCertData idpCertData = new IdpCertData();
        idpCertData.setCertData(Collections.singletonList(CERTIFICATE_TIM_LATEST));

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.singletonList(idpCertData))
                .when(sut)
                .getIdpCertData(any(Document.class));

        ErrorValidatingAssertionSignature e =
                assertThrows(
                        ErrorValidatingAssertionSignature.class,
                        () -> sut.validateLollipop(request));

        Assertions.assertEquals(
                ErrorValidatingAssertionSignature.ErrorCode.MISSING_ASSERTION_SIGNATURE,
                e.getErrorCode());
    }

    @SneakyThrows
    @Test
    void validateLollipopValidateSignatureFailureForSignatureNotValid() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_SIGNATURE);

        IdpCertData idpCertData = new IdpCertData();
        idpCertData.setCertData(Collections.singletonList(CERTIFICATE_TIM_LATEST));

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.singletonList(idpCertData))
                .when(sut)
                .getIdpCertData(any(Document.class));

        boolean result = sut.validateLollipop(request);

        assertFalse(result);
    }

    @SneakyThrows
    @Test
    void validateLollipopSuccess() {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(
                new String(Base64.decode(ASSERTION_XML_TIM.getBytes(StandardCharsets.UTF_8))));

        IdpCertData idpCertData = new IdpCertData();
        idpCertData.setCertData(Collections.singletonList(CERTIFICATE_TIM_LATEST));

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());
        doReturn(true).when(sut).validateAssertionPeriod(any(Document.class));
        doReturn(true)
                .when(sut)
                .validateUserId(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(true)
                .when(sut)
                .validateInResponseTo(any(LollipopConsumerRequest.class), any(Document.class));
        doReturn(Collections.singletonList(idpCertData))
                .when(sut)
                .getIdpCertData(any(Document.class));

        boolean result = sut.validateLollipop(request);

        assertTrue(result);
    }

    private LollipopConsumerRequest getLollipopConsumerRequest(
            String assertionRef, String publicKey, String userId) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(lollipopRequestConfigMock.getAuthJWTHeader(), "");
        headers.put(lollipopRequestConfigMock.getAssertionRefHeader(), assertionRef);
        headers.put(lollipopRequestConfigMock.getUserIdHeader(), userId);
        headers.put(lollipopRequestConfigMock.getPublicKeyHeader(), publicKey);

        return LollipopConsumerRequest.builder().headerParams(headers).build();
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.enumeration.HttpMessageVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import java.io.UnsupportedEncodingException;

import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.*;

class LollipopConsumerCommandImplTest {

    private static HttpMessageVerifierService messageVerifierServiceMock;
    private static AssertionVerifierService assertionVerifierServiceMock;
    private LollipopConsumerRequestValidationService requestValidationServiceMock;
    private static LollipopConsumerCommand sut;

    @BeforeEach
    void beforeAll() {
        messageVerifierServiceMock = Mockito.mock(HttpMessageVerifierService.class);
        assertionVerifierServiceMock = Mockito.mock(AssertionVerifierService.class);
        requestValidationServiceMock = Mockito.mock(LollipopConsumerRequestValidationService.class);
        sut =
                Mockito.spy(
                        new LollipopConsumerCommandImpl(
                                messageVerifierServiceMock,
                                assertionVerifierServiceMock,
                                requestValidationServiceMock
                        ));
    }

    @Test
    void failedHttpMessageValidationThrowDigestException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException,
                    LollipopSignatureException {

        doThrow(new LollipopDigestException(
                LollipopDigestException.ErrorCode.INCORRECT_DIGEST, "error"))
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                HttpMessageVerificationResultCode.DIGEST_VALIDATION_ERROR.name(),
                commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never())
                .validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedHttpMessageValidationThrowSignatureException()
            throws LollipopDigestException, UnsupportedEncodingException, LollipopVerifierException,
                    LollipopSignatureException {

        doThrow(
                        new LollipopSignatureException(
                                LollipopSignatureException.ErrorCode.INVALID_SIGNATURE, "error"))
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                HttpMessageVerificationResultCode.SIGNATURE_VALIDATION_ERROR.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never())
                .validateLollipop(any(LollipopConsumerRequest.class));
    }

    @SneakyThrows
    @Test
    void failedHttpMessageValidationThrowUnsupportedEncodingException() {

        doThrow(UnsupportedEncodingException.class)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                HttpMessageVerificationResultCode.UNSUPPORTED_ENCODING.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never())
                .validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedHttpMessageValidationWithoutThrowingException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException, LollipopSignatureException {

        doReturn(false)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_FAILED.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never())
                .validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationWithoutThrowingException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException, LollipopSignatureException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doReturn(false)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                AssertionVerificationResultCode.ASSERTION_VERIFICATION_FAILED.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void successLollipopRequestValidation()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException, LollipopSignatureException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doReturn(true)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals("SUCCESS", commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedLollipopRequestValidation()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException {


        doThrow(LollipopRequestContentValidationException.class).when(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals("REQUEST PARAMS VALIDATION FAILED", commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock, never()).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never()).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationThrowErrorRetrievingAssertionException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doThrow(ErrorRetrievingAssertionException.class)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                AssertionVerificationResultCode.ERROR_RETRIEVING_ASSERTION.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationThrowAssertionPeriodException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doThrow(AssertionPeriodException.class)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                AssertionVerificationResultCode.PERIOD_VALIDATION_ERROR.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationThrowAssertionThumbprintException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doThrow(AssertionThumbprintException.class)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                AssertionVerificationResultCode.THUMBPRINT_VALIDATION_ERROR.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationThrowAssertionUserIdException()
            throws LollipopDigestException, UnsupportedEncodingException,
            LollipopVerifierException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, LollipopRequestContentValidationException {

        doReturn(true)
                .when(messageVerifierServiceMock)
                .verifyHttpMessage(any(LollipopConsumerRequest.class));
        doThrow(AssertionUserIdException.class)
                .when(assertionVerifierServiceMock)
                .validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(LollipopConsumerRequest.builder().build());

        Assertions.assertEquals(
                AssertionVerificationResultCode.USER_ID_VALIDATION_ERROR.name(),
                commandResult.getResultCode());

        verify(requestValidationServiceMock).validateLollipopRequest(any(LollipopConsumerRequest.class));
        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }
}

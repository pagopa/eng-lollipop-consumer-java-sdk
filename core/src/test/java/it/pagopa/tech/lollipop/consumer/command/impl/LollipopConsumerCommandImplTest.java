package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.enumeration.HttpMessageVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.UnsupportedEncodingException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LollipopConsumerCommandImplTest {

    private static HttpMessageVerifierService messageVerifierServiceMock;
    private static AssertionVerifierService assertionVerifierServiceMock;
    private static LollipopConsumerCommand sut;

    @BeforeEach
    void beforeAll() {
        messageVerifierServiceMock = Mockito.mock(HttpMessageVerifierService.class);
        assertionVerifierServiceMock = Mockito.mock(AssertionVerifierService.class);
        sut = Mockito.spy(new LollipopConsumerCommandImpl(messageVerifierServiceMock, assertionVerifierServiceMock));
    }

    @Test
    void failedHttpMessageValidationThrowDigestException() throws LollipopDigestException, UnsupportedEncodingException {

        doThrow(new LollipopDigestException(LollipopDigestException.ErrorCode.INCORRECT_DIGEST, "error"))
                .when(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(new LollipopConsumerRequest());

        Assertions.assertEquals(HttpMessageVerificationResultCode.DIGEST_VALIDATION_ERROR.name(), commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never()).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedHttpMessageValidationThrowUnsupportedEncodingException() throws LollipopDigestException, UnsupportedEncodingException {

        doThrow(UnsupportedEncodingException.class).when(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(new LollipopConsumerRequest());

        Assertions.assertEquals(HttpMessageVerificationResultCode.UNSUPPORTED_ENCODING.name(), commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never()).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedHttpMessageValidationWithoutThrowingException() throws LollipopDigestException, UnsupportedEncodingException {

        doReturn(false).when(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(new LollipopConsumerRequest());

        Assertions.assertEquals(HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_FAILED.name(), commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock, never()).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void failedAssertionValidationWithoutThrowingException() throws LollipopDigestException, UnsupportedEncodingException {

        doReturn(true).when(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        doReturn(false).when(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(new LollipopConsumerRequest());

        Assertions.assertEquals(AssertionVerificationResultCode.ASSERTION_VERIFICATION_FAILED.name(), commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }

    @Test
    void successLollipopRequestValidation() throws LollipopDigestException, UnsupportedEncodingException {

        doReturn(true).when(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        doReturn(true).when(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));

        CommandResult commandResult = sut.doExecute(new LollipopConsumerRequest());

        Assertions.assertEquals("SUCCESS", commandResult.getResultCode());

        verify(messageVerifierServiceMock).verifyHttpMessage(any(LollipopConsumerRequest.class));
        verify(assertionVerifierServiceMock).validateLollipop(any(LollipopConsumerRequest.class));
    }
}
package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.enumeration.HttpMessageVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

/**
 * Implementation of the {@link LollipopConsumerCommand}
 */
public class LollipopConsumerCommandImpl implements LollipopConsumerCommand {

    private final HttpMessageVerifierService messageVerifierService;
    private final AssertionVerifierService assertionVerifierService;

    public static final String VERIFICATION_SUCCESS_CODE = "SUCCESS";

    @Inject
    public LollipopConsumerCommandImpl(HttpMessageVerifierService messageVerifierService, AssertionVerifierService assertionVerifierService) {
        this.messageVerifierService = messageVerifierService;
        this.assertionVerifierService = assertionVerifierService;
    }

    /**
     * Command that execute all necessary method for validating a Lollipop request: HTTP message verification and Saml assertion verification
     *
     * @param request LolliPop request
     * @return {@link CommandResult} object with result code and message of request verification
     */
    @Override
    public CommandResult doExecute(LollipopConsumerRequest request) {

        CommandResult messageVerificationResult = getHttpMessageVerificationResult(request);
        if (!messageVerificationResult.getResultCode().equals(HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_SUCCESS.name())) {
            return messageVerificationResult;
        }
        CommandResult assertionVerificationResult = getAssertionVerificationResult(request);
        if (!assertionVerificationResult.getResultCode().equals(AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name())) {
            return assertionVerificationResult;
        }
        return buildCommandResult(VERIFICATION_SUCCESS_CODE, "Verification completed successfully");
    }

    private CommandResult getAssertionVerificationResult(LollipopConsumerRequest request) {
        boolean result = assertionVerifierService.validateLollipop(request);

        if (!result) {
            return buildCommandResult(
                    AssertionVerificationResultCode.ASSERTION_VERIFICATION_FAILED.name(),
                    "Validation of SAML assertion failed, authentication failed"
            );
        }

        return buildCommandResult(
                AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name(),
                "SAML assertion validated successfully"
        );
    }

    private CommandResult getHttpMessageVerificationResult(LollipopConsumerRequest request) {
        boolean result;
        try {
            result = messageVerifierService.verifyHttpMessage(request);
        } catch (LollipopDigestException e) {
            String message = String.format("HTTP message validation failed on verifying digest with error code %s and message: %s", e.getErrorCode(), e.getMessage());
            return buildCommandResult(HttpMessageVerificationResultCode.DIGEST_VALIDATION_ERROR.name(), message);
        } catch (UnsupportedEncodingException e) {
            String message = String.format("HTTP message validation failed on encoding request body with message: %s", e.getMessage());
            return buildCommandResult(HttpMessageVerificationResultCode.UNSUPPORTED_ENCODING.name(), message);
        }

        if (!result) {
            return buildCommandResult(
                    HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_FAILED.name(),
                    "Validation of HTTP message failed, authentication failed"
            );
        }
        return buildCommandResult(
                HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_SUCCESS.name(),
                "HTTP message validated successfully"
        );
    }

    private CommandResult buildCommandResult(String resultCode, String message) {
        return new CommandResult(resultCode, message);
    }

}

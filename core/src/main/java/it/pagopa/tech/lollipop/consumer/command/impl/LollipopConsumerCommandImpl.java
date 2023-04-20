/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.enumeration.HttpMessageVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;

import java.io.UnsupportedEncodingException;
import javax.inject.Inject;

/** Implementation of the {@link LollipopConsumerCommand} */
public class LollipopConsumerCommandImpl implements LollipopConsumerCommand {

    private final LollipopConsumerRequestConfig lollipopConsumerRequestConfig;
    private final HttpMessageVerifierService messageVerifierService;
    private final AssertionVerifierService assertionVerifierService;
    private final LollipopConsumerRequestValidationService requestValidationService;
    private final LollipopConsumerRequest request;
    private final LollipopLoggerService lollipopLoggerService;

    private static final String CODE_AND_MESSAGE = " with error code: %s and message: %s";
    public static final String VERIFICATION_SUCCESS_CODE = "SUCCESS";
    public static final String REQUEST_PARAMS_VALIDATION_FAILED =
            "REQUEST PARAMS VALIDATION FAILED";

    @Inject
    public LollipopConsumerCommandImpl(
            LollipopConsumerRequestConfig lollipopConsumerRequestConfig,
            HttpMessageVerifierService messageVerifierService,
            AssertionVerifierService assertionVerifierService,
            LollipopConsumerRequestValidationService requestValidationService,
            LollipopLoggerService lollipopLoggerService,
        LollipopConsumerRequest lollipopConsumerRequest) {
        this.lollipopConsumerRequestConfig = lollipopConsumerRequestConfig;
        this.messageVerifierService = messageVerifierService;
        this.assertionVerifierService = assertionVerifierService;
        this.requestValidationService = requestValidationService;
        this.lollipopLoggerService = lollipopLoggerService;
        this.request = lollipopConsumerRequest;
    }

    /**
     * Command that execute all necessary method for validating a Lollipop request: HTTP message
     * verification and Saml assertion verification
     *
     * @return {@link CommandResult} object with result code and message of request verification
     */
    @Override
    public CommandResult doExecute() {

        CommandResult commandResult;

        try {
            requestValidationService.validateLollipopRequest(request);
        } catch (LollipopRequestContentValidationException e) {
            String message =
                    String.format(
                            "Error validating Lollipop request header or body, validation failed"
                                    + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(REQUEST_PARAMS_VALIDATION_FAILED, message);
        }

            CommandResult messageVerificationResult = getHttpMessageVerificationResult(request);
            if (!messageVerificationResult
                    .getResultCode()
                    .equals(HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_SUCCESS.name())) {
                logRequestAndResponse(request, messageVerificationResult);
                return messageVerificationResult;
            }
            CommandResult assertionVerificationResult = getAssertionVerificationResult(request);
            if (!assertionVerificationResult
                    .getResultCode()
                    .equals(AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name())) {
                logRequestAndResponse(request, assertionVerificationResult);
                return assertionVerificationResult;
            }

        } catch (LollipopRequestContentValidationException e) {
            String message =
                    String.format(
                            "Error validating Lollipop request header or body, validation failed"
                                    + " with error code %s and message: %s",
                            e.getErrorCode(), e.getMessage());
            commandResult = buildCommandResult(REQUEST_PARAMS_VALIDATION_FAILED, message);
            logRequestAndResponse(request, commandResult);
            return commandResult;
        }

        commandResult = buildCommandResult(VERIFICATION_SUCCESS_CODE, "Verification completed successfully");
        logRequestAndResponse(request, commandResult);
        return commandResult;
    }

    private CommandResult getAssertionVerificationResult(LollipopConsumerRequest request) {
        boolean result;
        try {
            result = assertionVerifierService.validateLollipop(request);
        } catch (ErrorRetrievingAssertionException e) {
            String message =
                    String.format(
                            "Cannot obtain the assertion, validation failed" + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.ERROR_RETRIEVING_ASSERTION.name(), message);
        } catch (AssertionPeriodException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying period" + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.PERIOD_VALIDATION_ERROR.name(), message);
        } catch (AssertionThumbprintException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying thumbprint"
                                    + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.THUMBPRINT_VALIDATION_ERROR.name(), message);
        } catch (AssertionUserIdException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying user id" + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.USER_ID_VALIDATION_ERROR.name(), message);
        } catch (ErrorValidatingAssertionSignature e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying signature" + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.SIGNATURE_VALIDATION_ERROR.name(), message);
        } catch (ErrorRetrievingIdpCertDataException e) {
            String message =
                    String.format(
                            "Assertion validation failed on retrieving identity provider's"
                                    + " certification data"
                                    + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.IDP_CERT_DATA_RETRIEVING_ERROR.name(), message);
        }

        if (!result) {
            return buildCommandResult(
                    AssertionVerificationResultCode.ASSERTION_VERIFICATION_FAILED.name(),
                    "Validation of SAML assertion failed, authentication failed");
        }

        return buildCommandResult(
                AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name(),
                "SAML assertion validated successfully");
    }

    private CommandResult getHttpMessageVerificationResult(LollipopConsumerRequest request) {
        boolean result;
        try {
            result = messageVerifierService.verifyHttpMessage(request);
        } catch (LollipopDigestException e) {
            String message =
                    String.format(
                            "HTTP message validation failed on verifying digest" + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    HttpMessageVerificationResultCode.DIGEST_VALIDATION_ERROR.name(), message);
        } catch (LollipopSignatureException e) {
            String message =
                    String.format(
                            "HTTP message validation failed on verifying signatures"
                                    + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    HttpMessageVerificationResultCode.SIGNATURE_VALIDATION_ERROR.name(), message);
        } catch (UnsupportedEncodingException e) {
            String message =
                    String.format(
                            "HTTP message validation failed on encoding request body with message:"
                                    + " %s",
                            e.getMessage());
            return buildCommandResult(
                    HttpMessageVerificationResultCode.UNSUPPORTED_ENCODING.name(), message);
        } catch (LollipopVerifierException e) {
            String message =
                    String.format(
                            "HTTP message validation failed on content validation"
                                    + CODE_AND_MESSAGE,
                            e.getErrorCode(),
                            e.getMessage());
            return buildCommandResult(
                    HttpMessageVerificationResultCode.REQUEST_VALIDATION_ERROR.name(), message);
        }

        if (!result) {
            return buildCommandResult(
                    HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_FAILED.name(),
                    "Validation of HTTP message failed, authentication failed");
        }
        return buildCommandResult(
                HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_SUCCESS.name(),
                "HTTP message validated successfully");
    }

    private CommandResult buildCommandResult(String resultCode, String message) {
        return new CommandResult(resultCode, message);
    }

    private void logRequestAndResponse(LollipopConsumerRequest lollipopConsumerRequest, CommandResult commandResult) {
        if (lollipopConsumerRequestConfig.isEnableConsumerLogging()) {
            lollipopLoggerService.log("Lollipop validation for request: {} completed with the result: {}",
                    lollipopConsumerRequest, commandResult);
        }

    }

}

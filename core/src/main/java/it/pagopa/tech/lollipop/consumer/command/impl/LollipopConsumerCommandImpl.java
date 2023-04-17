/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.enumeration.HttpMessageVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import java.io.UnsupportedEncodingException;
import javax.inject.Inject;

/** Implementation of the {@link LollipopConsumerCommand} */
public class LollipopConsumerCommandImpl implements LollipopConsumerCommand {

    private final HttpMessageVerifierService messageVerifierService;
    private final AssertionVerifierService assertionVerifierService;
    private final LollipopConsumerRequestValidationService requestValidationService;

    private final LollipopConsumerRequest request;

    public static final String VERIFICATION_SUCCESS_CODE = "SUCCESS";
    public static final String REQUEST_PARAMS_VALIDATION_FAILED =
            "REQUEST PARAMS VALIDATION FAILED";

    @Inject
    public LollipopConsumerCommandImpl(
            HttpMessageVerifierService messageVerifierService,
            AssertionVerifierService assertionVerifierService,
            LollipopConsumerRequestValidationService requestValidationService,
            LollipopConsumerRequest lollipopConsumerRequest) {
        this.messageVerifierService = messageVerifierService;
        this.assertionVerifierService = assertionVerifierService;
        this.requestValidationService = requestValidationService;
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

        try {
            requestValidationService.validateLollipopRequest(request);
        } catch (LollipopRequestContentValidationException e) {
            String message =
                    String.format(
                            "Error validating Lollipop request header or body, validation failed"
                                    + " with error code %s and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(REQUEST_PARAMS_VALIDATION_FAILED, message);
        }

        CommandResult messageVerificationResult = getHttpMessageVerificationResult(request);
        if (!messageVerificationResult
                .getResultCode()
                .equals(HttpMessageVerificationResultCode.HTTP_MESSAGE_VALIDATION_SUCCESS.name())) {
            return messageVerificationResult;
        }
        CommandResult assertionVerificationResult = getAssertionVerificationResult(request);
        if (!assertionVerificationResult
                .getResultCode()
                .equals(AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name())) {
            return assertionVerificationResult;
        }
        return buildCommandResult(VERIFICATION_SUCCESS_CODE, "Verification completed successfully");
    }

    private CommandResult getAssertionVerificationResult(LollipopConsumerRequest request) {
        boolean result;
        try {
            result = assertionVerifierService.validateLollipop(request);
        } catch (ErrorRetrievingAssertionException e) {
            String message =
                    String.format(
                            "Cannot obtain the assertion, validation failed with error code %s"
                                    + " and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.ERROR_RETRIEVING_ASSERTION.name(), message);
        } catch (AssertionPeriodException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying period with error code %s"
                                    + " and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.PERIOD_VALIDATION_ERROR.name(), message);
        } catch (AssertionThumbprintException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying thumbprint  with error code"
                                    + " %s and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.THUMBPRINT_VALIDATION_ERROR.name(), message);
        } catch (AssertionUserIdException e) {
            String message =
                    String.format(
                            "Assertion validation failed on verifying user id  with error code %s"
                                    + " and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(
                    AssertionVerificationResultCode.USER_ID_VALIDATION_ERROR.name(), message);
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
                            "HTTP message validation failed on verifying digest with error code %s"
                                    + " and message: %s",
                            e.getErrorCode(), e.getMessage());
            return buildCommandResult(
                    HttpMessageVerificationResultCode.DIGEST_VALIDATION_ERROR.name(), message);
        } catch (LollipopSignatureException e) {
            String message =
                    String.format(
                            "HTTP message validation failed on verifying signatures with message:"
                                    + " %s",
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
                            "HTTP message validation failed on content validation with message:"
                                    + " %s",
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
}

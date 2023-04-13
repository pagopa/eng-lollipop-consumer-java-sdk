/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.exception;

import java.util.Objects;

/**
 * Thrown in case of problems when computing or verifying lollipop request through the
 * http-signature flow
 */
public class LollipopVerifierException extends Exception {

    /** Error code of this exception */
    private final ErrorCode errorCode;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message Detail message
     */
    public LollipopVerifierException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message Detail message
     * @param cause Exception causing the constructed one
     */
    public LollipopVerifierException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode);
    }

    /**
     * Returns error code
     *
     * @return Error code of this exception
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /** Error codes to classify Lollipop Request Exceptions */
    public enum ErrorCode {
        MISSING_SIGNATURE,
        INVALID_SIGNATURE_HEADER,

        MISSING_SIGNATURE_INPUT,
        INVALID_SIGNATURE_INPUT,

        MISSING_ASSERTION_REF,
        INVALID_ASSERTION_REF,

        MISSING_ASSERTION_TYPE,
        INVALID_ASSERTION_TYPE,

        MISSING_USER_ID,
        INVALID_USER_ID,

        MISSING_AUTH_JWT,
        INVALID_AUTH_JWT,

        MISSING_ORIGINAL_METHOD,
        INVALID_ORIGINAL_METHOD,

        MISSING_ORIGINAL_URL,
        INVALID_ORIGINAL_URL,

        MISSING_PUBLIC_KEY,
        INVALID_PUBLIC_KEY
    }
}

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

        MISSING_SIGNATURE_INPUT
    }
}

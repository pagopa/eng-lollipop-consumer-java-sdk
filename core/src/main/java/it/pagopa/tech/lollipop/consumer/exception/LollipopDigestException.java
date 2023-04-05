/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.exception;

import java.util.Objects;

/**
 * Thrown in case of problems when computing or verifying digest, or when verified digest is
 * incorrect
 */
public class LollipopDigestException extends Exception {

    /** Error code of this exception */
    private final ErrorCode errorCode;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message Detail message
     */
    public LollipopDigestException(ErrorCode errorCode, String message) {
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
    public LollipopDigestException(ErrorCode errorCode, String message, Throwable cause) {
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

    /** Error codes to classify Digest Exceptions */
    public enum ErrorCode {
        MISSING_DIGEST,
        MISSING_PAYLOAD,
        /**
         * No supported hash algorithms detected when verifying or processing
         * <em>Want-...-Digest</em> headers.
         */
        UNSUPPORTED_ALGORITHM,

        /** When verifying, provided digest is different from the computed one */
        INCORRECT_DIGEST,

        /**
         * Parsed <em>...-Digest</em> or <em>Want-...-Digest</em> header is not syntactically
         * correct
         */
        INVALID_HEADER,
    }
}

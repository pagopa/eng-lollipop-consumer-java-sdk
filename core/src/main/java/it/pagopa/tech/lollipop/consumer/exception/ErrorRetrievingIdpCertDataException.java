/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.exception;

import java.util.Objects;

/** Thrown in case of problems on retrieving the IDP data */
public class ErrorRetrievingIdpCertDataException extends Exception {

    /** Error code of this exception */
    private final ErrorCode errorCode;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message Detail message
     */
    public ErrorRetrievingIdpCertDataException(ErrorCode errorCode, String message) {
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
    public ErrorRetrievingIdpCertDataException(
            ErrorCode errorCode, String message, Throwable cause) {
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
        ENTITY_ID_FIELD_NOT_FOUND,
        INSTANT_FIELD_NOT_FOUND,
        IDP_CERT_DATA_NOT_FOUND
    }
}

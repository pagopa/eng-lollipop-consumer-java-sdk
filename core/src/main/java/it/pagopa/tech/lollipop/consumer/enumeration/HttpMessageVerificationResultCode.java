/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.enumeration;

/** Result codes to classify HTTP message validation */
public enum HttpMessageVerificationResultCode {

    /** Request validation failed throwing an exception */
    REQUEST_VALIDATION_ERROR,
    /** Digest validation failed throwing an exception */
    DIGEST_VALIDATION_ERROR,

    /** No supported encoding detected when encoding request body */
    UNSUPPORTED_ENCODING,

    /** HTTP message validation failed without throwing an exception */
    HTTP_MESSAGE_VALIDATION_FAILED,

    /** HTTP message validation completed successfully */
    HTTP_MESSAGE_VALIDATION_SUCCESS
}

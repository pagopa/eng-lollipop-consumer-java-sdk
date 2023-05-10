/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.enumeration;

/** Result codes to classify Saml assertion validation */
public enum AssertionVerificationResultCode {

    /** Saml assertion validation failed on retrieving the assertion */
    ERROR_RETRIEVING_ASSERTION,

    /** Saml assertion validation failed on period validation */
    PERIOD_VALIDATION_ERROR,

    /** Saml assertion validation failed on user id validation */
    USER_ID_VALIDATION_ERROR,

    /** Saml assertion validation failed on thumbprint validation */
    THUMBPRINT_VALIDATION_ERROR,

    /** Saml assertion validation failed on signature validation */
    SIGNATURE_VALIDATION_ERROR,

    /** Saml assertion validation failed on retrieving identity provider's certification data */
    IDP_CERT_DATA_RETRIEVING_ERROR,

    /** Saml assertion validation failed without throwing an exception */
    ASSERTION_VERIFICATION_FAILED,

    /** Saml assertion validation completed successfully */
    ASSERTION_VERIFICATION_SUCCESS
}

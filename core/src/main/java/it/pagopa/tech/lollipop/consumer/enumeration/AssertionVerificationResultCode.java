/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.enumeration;

/** Result codes to classify Saml assertion validation */
public enum AssertionVerificationResultCode {

    /** Saml assertion validation failed without throwing an exception */
    ASSERTION_VERIFICATION_FAILED,

    /** Saml assertion validation completed successfully */
    ASSERTION_VERIFICATION_SUCCESS
}

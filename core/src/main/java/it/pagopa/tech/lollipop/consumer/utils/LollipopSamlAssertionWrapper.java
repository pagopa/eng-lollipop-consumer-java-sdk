/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.utils;

import it.pagopa.tech.lollipop.consumer.exception.ErrorValidatingAssertionSignature;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.saml.SAMLKeyInfo;
import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Element;

/**
 * Extension of the {@link SamlAssertionWrapper} class that add the check for the existence of
 * signature in the assertion before its validation
 */
public class LollipopSamlAssertionWrapper extends SamlAssertionWrapper {

    public LollipopSamlAssertionWrapper(Element element) throws WSSecurityException {
        super(element);
    }

    /**
     * If the assertion has a signature call the signature validation, otherwise it throws {@link
     * ErrorValidatingAssertionSignature}
     *
     * @see SamlAssertionWrapper#verifySignature(SAMLKeyInfo)
     * @throws ErrorValidatingAssertionSignature if the assertion does not have a signature
     */
    public void verifySignatureLollipop(SAMLKeyInfo samlKeyInfo)
            throws WSSecurityException, ErrorValidatingAssertionSignature {
        Signature sig = getSignature();
        if (sig == null) {
            throw new ErrorValidatingAssertionSignature(
                    ErrorValidatingAssertionSignature.ErrorCode.MISSING_ASSERTION_SIGNATURE,
                    "The given assertion does not have a signature");
        }
        verifySignature(samlKeyInfo);
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import javax.inject.Inject;

public class AssertionVerifierServiceImpl implements AssertionVerifierService {

    private IdpCertProvider idpCertProvider;
    private AssertionService assertionService;

    @Inject
    public AssertionVerifierServiceImpl(
            IdpCertProvider idpCertProvider, AssertionService assertionService) {
        this.idpCertProvider = idpCertProvider;
        this.assertionService = assertionService;
    }

    @Override
    public boolean validateLollipop(LollipopConsumerRequest request) {
        return false;
    }

    private SamlAssertion getAssertion(String jwt, String assertionRef) {
        return null;
    }

    private boolean validateAssertionPeriod(String notBefore) {
        return false;
    }

    private boolean validateThumbprint(LollipopConsumerRequest request, SamlAssertion assertion) {
        return false;
    }

    private boolean validateInResponseTo(LollipopConsumerRequest request, SamlAssertion assertion) {
        return false;
    }

    private IdpCertData getIdpCertData(SamlAssertion assertion) {
        return null;
    }

    private boolean validateSignature(SamlAssertion assertion, IdpCertData idpCertData) {
        return false;
    }
}

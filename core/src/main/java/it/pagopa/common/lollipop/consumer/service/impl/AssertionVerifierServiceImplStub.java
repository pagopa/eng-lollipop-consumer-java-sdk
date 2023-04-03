package it.pagopa.common.lollipop.consumer.service.impl;

import it.pagopa.common.lollipop.consumer.assertion.AssertionService;
import it.pagopa.common.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.common.lollipop.consumer.model.IdpCertData;
import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.common.lollipop.consumer.model.SamlAssertion;
import it.pagopa.common.lollipop.consumer.service.AssertionVerifierService;

public class AssertionVerifierServiceImplStub implements AssertionVerifierService {

    private IdpCertProvider idpCertProvider;
    private AssertionService assertion;
    @Override
    public boolean validateLollipop(LollipopConsumerRequest request) {
        return false;
    }

    private SamlAssertion getAssertion(String jwt, String assertionRef) { return null; }
    private boolean validateAssertionPeriod(String notBefore) { return false; }
    private boolean validateThumbprint(LollipopConsumerRequest request, SamlAssertion assertion) { return false; }
    private boolean validateInResponseTo(LollipopConsumerRequest request, SamlAssertion assertion) { return false; }
    private IdpCertData getIdpCertData(SamlAssertion assertion) { return null; }
    private boolean validateSignature(SamlAssertion assertion, IdpCertData idpCertData) { return false; }

}

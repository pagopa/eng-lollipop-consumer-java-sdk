package it.pagopa.spid.common.lc.service.impl;

import it.pagopa.spid.common.lc.assertion.AssertionService;
import it.pagopa.spid.common.lc.idp.IdpCertProvider;
import it.pagopa.spid.common.lc.model.IdpCertData;
import it.pagopa.spid.common.lc.model.LollipopConsumerRequest;
import it.pagopa.spid.common.lc.model.SamlAssertion;
import it.pagopa.spid.common.lc.service.AssertionVerifierService;

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

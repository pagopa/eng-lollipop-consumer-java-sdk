package it.pagopa.spid.common.lc.assertion.impl;

import it.pagopa.spid.common.lc.assertion.AssertionService;
import it.pagopa.spid.common.lc.assertion.client.AssertionClient;
import it.pagopa.spid.common.lc.assertion.storage.AssertionStorage;
import it.pagopa.spid.common.lc.model.SamlAssertion;

public class AssertionServiceImplStub implements AssertionService {

    private AssertionStorage assertionStorage;
    private AssertionClient assertionClient;
    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef) {
        return null;
    }
}

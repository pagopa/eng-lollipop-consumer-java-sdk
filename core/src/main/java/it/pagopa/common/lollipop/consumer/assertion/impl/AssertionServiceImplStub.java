package it.pagopa.common.lollipop.consumer.assertion.impl;

import it.pagopa.common.lollipop.consumer.assertion.AssertionService;
import it.pagopa.common.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.common.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.common.lollipop.consumer.model.SamlAssertion;

public class AssertionServiceImplStub implements AssertionService {

    private AssertionStorage assertionStorage;
    private AssertionClient assertionClient;
    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef) {
        return null;
    }
}

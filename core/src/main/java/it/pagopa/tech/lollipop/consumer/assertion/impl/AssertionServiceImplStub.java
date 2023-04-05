/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

public class AssertionServiceImplStub implements AssertionService {

    private AssertionStorage assertionStorage;
    private AssertionClient assertionClient;

    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef) {
        return null;
    }
}

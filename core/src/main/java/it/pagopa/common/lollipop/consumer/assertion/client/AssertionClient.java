package it.pagopa.common.lollipop.consumer.assertion.client;

import it.pagopa.common.lollipop.consumer.model.SamlAssertion;

public interface AssertionClient {

    SamlAssertion getAssertion(String jwt, String assertionRef);
}

package it.pagopa.tech.lollipop.consumer.assertion.client;

import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

public interface AssertionClient {

    SamlAssertion getAssertion(String jwt, String assertionRef) throws LollipopAssertionNotFoundException;
}

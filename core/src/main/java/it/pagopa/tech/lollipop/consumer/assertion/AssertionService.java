package it.pagopa.tech.lollipop.consumer.assertion;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

public interface AssertionService {

    SamlAssertion getAssertion(String jwt, String assertionRef);
}

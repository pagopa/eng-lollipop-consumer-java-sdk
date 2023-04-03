package it.pagopa.common.lollipop.consumer.assertion;

import it.pagopa.common.lollipop.consumer.model.SamlAssertion;

public interface AssertionService {

    SamlAssertion getAssertion(String jwt, String assertionRef);
}

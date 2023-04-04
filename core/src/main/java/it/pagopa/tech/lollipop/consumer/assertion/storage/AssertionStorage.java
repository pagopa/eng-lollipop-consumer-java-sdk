package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

public interface AssertionStorage {

    SamlAssertion getAssertion(String assertionRef);
    void saveAssertion(SamlAssertion assertion);

}

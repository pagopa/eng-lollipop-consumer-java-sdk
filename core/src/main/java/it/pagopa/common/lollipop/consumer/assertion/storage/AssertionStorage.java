package it.pagopa.common.lollipop.consumer.assertion.storage;

import it.pagopa.common.lollipop.consumer.model.SamlAssertion;

public interface AssertionStorage {

    SamlAssertion getAssertion(String assertionRef);
    void saveAssertion(SamlAssertion assertion);

}

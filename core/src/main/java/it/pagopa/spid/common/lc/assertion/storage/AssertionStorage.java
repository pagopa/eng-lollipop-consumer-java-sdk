package it.pagopa.spid.common.lc.assertion.storage;

import it.pagopa.spid.common.lc.model.SamlAssertion;

public interface AssertionStorage {

    SamlAssertion getAssertion(String assertionRef);
    void saveAssertion(SamlAssertion assertion);

}

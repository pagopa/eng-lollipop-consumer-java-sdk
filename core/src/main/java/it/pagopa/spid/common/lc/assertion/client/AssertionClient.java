package it.pagopa.spid.common.lc.assertion.client;

import it.pagopa.spid.common.lc.model.SamlAssertion;

public interface AssertionClient {

    SamlAssertion getAssertion(String jwt, String assertionRef);
}

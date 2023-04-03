package it.pagopa.spid.common.lc.assertion;

import it.pagopa.spid.common.lc.model.SamlAssertion;

public interface AssertionService {

    SamlAssertion getAssertion(String jwt, String assertionRef);
}

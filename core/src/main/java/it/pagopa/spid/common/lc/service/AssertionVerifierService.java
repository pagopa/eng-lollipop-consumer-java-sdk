package it.pagopa.spid.common.lc.service;

import it.pagopa.spid.common.lc.model.LollipopConsumerRequest;

public interface AssertionVerifierService {

    boolean validateLollipop(LollipopConsumerRequest request);
}

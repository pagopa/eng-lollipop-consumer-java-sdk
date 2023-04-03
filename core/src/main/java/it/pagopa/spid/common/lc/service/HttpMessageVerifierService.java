package it.pagopa.spid.common.lc.service;

import it.pagopa.spid.common.lc.model.LollipopConsumerRequest;

public interface HttpMessageVerifierService {

    boolean verifyHttpMessage(LollipopConsumerRequest request);
}

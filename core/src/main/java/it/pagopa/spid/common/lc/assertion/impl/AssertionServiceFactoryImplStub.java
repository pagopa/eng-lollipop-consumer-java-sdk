package it.pagopa.spid.common.lc.assertion.impl;

import it.pagopa.spid.common.lc.assertion.AssertionService;
import it.pagopa.spid.common.lc.assertion.AssertionServiceFactory;
import it.pagopa.spid.common.lc.assertion.client.AssertionClientProvider;
import it.pagopa.spid.common.lc.assertion.storage.AssertionStorageProvider;

public class AssertionServiceFactoryImplStub implements AssertionServiceFactory {
    private AssertionStorageProvider assertionStorageProvider;
    private AssertionClientProvider assertionClientProvider;
    @Override
    public AssertionService create() {
        return null;
    }
}

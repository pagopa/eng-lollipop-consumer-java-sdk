package it.pagopa.common.lollipop.consumer.assertion.impl;

import it.pagopa.common.lollipop.consumer.assertion.AssertionService;
import it.pagopa.common.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.common.lollipop.consumer.assertion.storage.AssertionStorageProvider;
import it.pagopa.common.lollipop.consumer.assertion.client.AssertionClientProvider;

public class AssertionServiceFactoryImplStub implements AssertionServiceFactory {
    private AssertionStorageProvider assertionStorageProvider;
    private AssertionClientProvider assertionClientProvider;
    @Override
    public AssertionService create() {
        return null;
    }
}

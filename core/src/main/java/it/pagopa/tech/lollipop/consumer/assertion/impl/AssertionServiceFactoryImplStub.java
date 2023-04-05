/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorageProvider;

public class AssertionServiceFactoryImplStub implements AssertionServiceFactory {
    private AssertionStorageProvider assertionStorageProvider;
    private AssertionClientProvider assertionClientProvider;

    @Override
    public AssertionService create() {
        return null;
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import javax.inject.Inject;

/**
 * Implementation of {@link AssertionServiceFactory}, used to create instances of {@link
 * AssertionServiceImpl}
 */
public class AssertionServiceFactoryImpl implements AssertionServiceFactory {
    private final AssertionStorageProvider assertionStorageProvider;
    private final AssertionClientProvider assertionClientProvider;
    private final StorageConfig storageConfig;

    @Inject
    public AssertionServiceFactoryImpl(
            AssertionStorageProvider assertionStorageProvider,
            AssertionClientProvider assertionClientProvider,
            StorageConfig storageConfig) {
        this.assertionStorageProvider = assertionStorageProvider;
        this.assertionClientProvider = assertionClientProvider;
        this.storageConfig = storageConfig;
    }

    /**
     * Factory for creating an instance of {@link AssertionServiceImpl}
     *
     * @return an instance of {@link AssertionServiceImpl}
     */
    @Override
    public AssertionService create() {
        return new AssertionServiceImpl(
                assertionStorageProvider.provideStorage(storageConfig),
                assertionClientProvider.provideClient());
    }
}

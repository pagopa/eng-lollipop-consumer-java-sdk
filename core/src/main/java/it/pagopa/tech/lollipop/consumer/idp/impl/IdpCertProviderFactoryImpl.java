/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;
import javax.inject.Inject;

/**
 * Implementation of {@link IdpCertProviderFactory}, used to create instances of {@link
 * IdpCertProviderImpl}
 */
public class IdpCertProviderFactoryImpl implements IdpCertProviderFactory {

    private IdpCertClientProvider idpCertClientProvider;
    private IdpCertStorageProvider idpCertStorageProvider;
    private final IdpCertStorageConfig storageConfig;

    @Inject
    public IdpCertProviderFactoryImpl(
            IdpCertStorageProvider idpCertStorageProvider,
            IdpCertClientProvider idpCertClientProvider,
            IdpCertStorageConfig storageConfig) {
        this.idpCertClientProvider = idpCertClientProvider;
        this.idpCertStorageProvider = idpCertStorageProvider;
        this.storageConfig = storageConfig;
    }

    /**
     * Factory for creating an instance of {@link IdpCertProvider}
     *
     * @return an instance of {@link IdpCertProviderImpl}
     */
    @Override
    public IdpCertProvider create() {
        return new IdpCertProviderImpl(
                idpCertClientProvider.provideClient(),
                idpCertStorageProvider.provideStorage(storageConfig));
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.storage;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;
import java.util.HashMap;

/** Implementation of {@link IdpCertStorageProvider} interface. It provides an instance of the */
public class SimpleIdpCertStorageProvider implements IdpCertStorageProvider {

    /**
     * {@inheritDoc}
     *
     * @param storageConfig the storage configuration
     * @return an instance of {@link SimpleIdpCertStorage}
     */
    @Override
    public IdpCertStorage provideStorage(IdpCertStorageConfig storageConfig) {
        return new SimpleIdpCertStorage(new HashMap<>(), new HashMap<>(), storageConfig);
    }
}

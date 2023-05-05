/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.idp;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorageProvisioner;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Implementation of {@link IdpCertStorageProvider} interface. It provides an instance of {@link
 * RedisIdpCertStorage}
 */
@Data
@AllArgsConstructor
public class RedisIdpCertStorageProvider implements IdpCertStorageProvider {

    private RedisStorageProvisioner redisStorageProvisioner;

    /**
     * {@inheritDoc}
     *
     * @param storageConfig the storage configuration
     * @return an instance of {@link RedisIdpCertStorage}
     */
    @Override
    public IdpCertStorage provideStorage(IdpCertStorageConfig storageConfig) {
        return new RedisIdpCertStorage(redisStorageProvisioner.getStorage(), storageConfig);
    }
}

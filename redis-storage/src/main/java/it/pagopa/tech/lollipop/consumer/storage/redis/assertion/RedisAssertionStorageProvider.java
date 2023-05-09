/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.assertion;

import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorageProvisioner;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Implementation of {@link AssertionStorageProvider} interface. It provides an instance of {@link
 * RedisAssertionStorage}
 */
@Data
@AllArgsConstructor
public class RedisAssertionStorageProvider implements AssertionStorageProvider {

    private RedisStorageProvisioner redisStorageProvisioner;

    /**
     * {@inheritDoc}
     *
     * @param storageConfig the storage configuration
     * @return an instance of {@link RedisAssertionStorage}
     */
    @Override
    public AssertionStorage provideStorage(StorageConfig storageConfig) {
        return new RedisAssertionStorage(redisStorageProvisioner.getStorage(), storageConfig);
    }
}

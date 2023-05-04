/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.idp;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

/**
 * Implementation of the {@link IdpCertStorage} interface as a simple in memory storage.
 *
 * <p>The storage can be configured via the {@link IdpCertStorageConfig} configuration class.
 *
 * <p>It store in a in memory {@link java.util.HashMap} the assertions and the associated scheduled
 * eviction operations, every time an assertion is accessed the associated eviction operation is
 * rescheduled.
 */
public class RedisIdpCertStorage implements IdpCertStorage {

    private final RedisStorage redisStorage;
    private final IdpCertStorageConfig storageConfig;

    public RedisIdpCertStorage(
            RedisStorage redisStorage, IdpCertStorageConfig idpCertStorageConfig) {
        this.redisStorage = redisStorage;
        this.storageConfig = idpCertStorageConfig;
    }

    /**
     * Retrieve the idpCertData associated with the provided tag if the storage is enabled {@link
     * IdpCertStorageConfig}, otherwise no operation is performed.
     *
     * <p>Before the list of idpCertData is returned the associated eviction operation is
     * rescheduled with the delay configured via {@link IdpCertStorageConfig}
     *
     * @param tag the idpCertData issue instant
     * @return the list of cert data if found, null if no cert data are present in the storage or
     *     the storage is disabled
     */
    @Override
    public IdpCertData getIdpCertData(String tag) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return null;
        }

        return (IdpCertData) redisStorage.get(tag);
    }

    /**
     * Store the idpCertData if the storage is enabled {@link IdpCertStorageConfig}, otherwise no
     * operation is performed.
     *
     * <p>Once the idpCertData is stored an eviction operation is scheduled with a delay configured
     * via {@link IdpCertStorageConfig}
     *
     * @param tag the idpCertData issue instant
     * @param idpCertData
     */
    @Override
    public void saveIdpCertData(String tag, IdpCertData idpCertData) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return;
        }

        redisStorage.save(tag, idpCertData);
    }
}

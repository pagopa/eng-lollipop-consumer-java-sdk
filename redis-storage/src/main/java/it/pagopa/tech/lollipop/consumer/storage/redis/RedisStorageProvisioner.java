/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis;

import it.pagopa.tech.lollipop.consumer.storage.redis.builder.ClientBuilder;

/**
 * This class provides a simple way to generate an instance of a RedisStorage, from the provided
 * ClientBuilder interface
 */
public class RedisStorageProvisioner {

    private final ClientBuilder clientBuilder;

    public RedisStorageProvisioner(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Provides an instance of the RedisStorage, produced by the defined clientBuilder
     *
     * @return an instance of the RedisStorage
     */
    public RedisStorage getStorage() {
        return clientBuilder.createStorage();
    }
}

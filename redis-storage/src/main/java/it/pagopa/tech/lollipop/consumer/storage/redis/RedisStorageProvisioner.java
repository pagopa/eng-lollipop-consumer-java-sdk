/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis;

import it.pagopa.tech.lollipop.consumer.storage.redis.builder.ClientBuilder;

public class RedisStorageProvisioner {

    private final ClientBuilder clientBuilder;

    public RedisStorageProvisioner(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    public RedisStorage getStorage() {
        return clientBuilder.createStorage();
    }
}

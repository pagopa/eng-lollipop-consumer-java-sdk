/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.builder;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import it.pagopa.tech.lollipop.consumer.storage.redis.storage.SimpleRedisStorage;

public class DefaultRedisClientBuilder implements ClientBuilder {

    @Override
    public RedisStorage createStorage() {
        return new SimpleRedisStorage(createRedisClient("TBD", 9000, "TBD", "TBD"));
    }

    private RedisClient createRedisClient(
            String hostName, int port, String username, String password) {

        // Build Redis URI with host and authentication details.
        RedisURI redisURI =
                RedisURI.Builder.redis(hostName)
                        .withPort(port)
                        .withSsl(true)
                        .withAuthentication(username, password)
                        .withClientName("LettuceClient")
                        .build();

        // Create Lettuce Redis Client
        RedisClient client = RedisClient.create(redisURI);

        // Configure the client options.
        client.setOptions(
                ClientOptions.builder()
                        .autoReconnect(true)
                        .socketOptions(SocketOptions.builder().keepAlive(true).build())
                        .build());

        return client;
    }

    private RedisClusterClient createRedisClusterClient(
            String hostName, int port, String username, String password) {

        // Build Redis URI with host and authentication details.
        RedisURI redisURI =
                RedisURI.Builder.redis(hostName)
                        .withPort(port)
                        .withSsl(true)
                        .withAuthentication(username, password)
                        .withClientName("LettuceClient")
                        .build();

        // Create Lettuce Redis Client
        RedisClusterClient client = RedisClusterClient.create(redisURI);

        // Configure the client options.
        client.setOptions(
                ClusterClientOptions.builder()
                        .autoReconnect(true)
                        .socketOptions(SocketOptions.builder().keepAlive(true).build())
                        .build());

        return client;
    }
}

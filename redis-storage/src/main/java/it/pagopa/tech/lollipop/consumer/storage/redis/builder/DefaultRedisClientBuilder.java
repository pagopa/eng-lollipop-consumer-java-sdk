/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.builder;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import it.pagopa.tech.lollipop.consumer.storage.redis.config.RedisStorageConfig;
import it.pagopa.tech.lollipop.consumer.storage.redis.storage.ClusteredRedisStorage;
import it.pagopa.tech.lollipop.consumer.storage.redis.storage.SimpleRedisStorage;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link ClientBuilder} interface, to be used in order to define instances
 * of Lettuce Redis clients
 */
public class DefaultRedisClientBuilder implements ClientBuilder {

    private final RedisStorageConfig redisStorageConfig;

    public DefaultRedisClientBuilder(RedisStorageConfig redisStorageConfig) {
        this.redisStorageConfig = redisStorageConfig;
    }

    /**
     * Creates an instance of {@link RedisStorage} using the generated RedisClient, depending on the
     * configured parameter it will be used a Clustered or Simple instance
     *
     * @return an instance of a RedisStorage
     */
    @Override
    public RedisStorage createStorage() {
        return redisStorageConfig.isClusterConnection()
                ? new ClusteredRedisStorage(
                        createRedisClusterClient(),
                        redisStorageConfig.getDefaultDelay(),
                        redisStorageConfig.isWithConnectionPooling())
                : new SimpleRedisStorage(
                        createRedisClient(),
                        redisStorageConfig.getDefaultDelay(),
                        redisStorageConfig.isWithConnectionPooling());
    }

    /**
     * Creates a simple Redis Client using the provided configurations
     *
     * @return a RedisClient
     */
    private RedisClient createRedisClient() {

        // Build Redis URI with host and authentication details.

        RedisURI redisURI =
                getRedisURI(
                        redisStorageConfig.getMainNode().getHostname(),
                        redisStorageConfig.getMainNode().getPort());

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

    /**
     * Creates a Clustered Redis Client using the provided configurations
     *
     * @return a RedisClient
     */
    private RedisClusterClient createRedisClusterClient() {

        List<RedisURI> redisURIList = new ArrayList<>();
        redisURIList.add(
                getRedisURI(
                        redisStorageConfig.getMainNode().getHostname(),
                        redisStorageConfig.getMainNode().getPort()));

        if (redisStorageConfig.getClusterNodeList() != null) {
            for (RedisStorageConfig.RedisNode redisNode : redisStorageConfig.getClusterNodeList()) {
                redisURIList.add(getRedisURI(redisNode.getHostname(), redisNode.getPort()));
            }
        }

        // Create Lettuce Redis Client
        RedisClusterClient client = RedisClusterClient.create(redisURIList);

        // Configure the client options.
        client.setOptions(
                ClusterClientOptions.builder()
                        .autoReconnect(true)
                        .socketOptions(SocketOptions.builder().keepAlive(true).build())
                        .build());

        return client;
    }

    private RedisURI getRedisURI(String hostname, Integer port) {
        RedisURI.Builder builder =
                !redisStorageConfig.isWithSentinel()
                        ? RedisURI.Builder.redis(hostname).withPort(port)
                        : RedisURI.Builder.sentinel(hostname, port);

        builder.withSsl(redisStorageConfig.isWithSsl());

        if (redisStorageConfig.getSentinelHostList() != null
                && !redisStorageConfig.isClusterConnection()) {
            redisStorageConfig.getSentinelHostList().forEach(builder::withSentinel);
        }

        if (redisStorageConfig.getMasterIds() != null
                && !redisStorageConfig.isClusterConnection()) {
            redisStorageConfig.getMasterIds().forEach(builder::withSentinelMasterId);
        }

        if (redisStorageConfig.isWithAuth()) {
            builder.withAuthentication(
                    redisStorageConfig.getUsername(), redisStorageConfig.getPassword());
        }

        builder.withClientName(redisStorageConfig.getClientName());

        return builder.build();
    }
}

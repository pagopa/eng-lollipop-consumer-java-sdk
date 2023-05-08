/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Getter
@Setter
/** Implements commands to a Redis Cluster using Lettuce */
public class ClusteredRedisStorage extends RedisStorage {

    private Long defaultDelayTimeSeconds = 60L;

    private Boolean withConnectionPool = false;

    public ClusteredRedisStorage(RedisClusterClient redisClient) {
        super(redisClient);
    }

    public ClusteredRedisStorage(RedisClusterClient redisClient, Long defaultDelayTimeSeconds) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
    }

    public ClusteredRedisStorage(
            RedisClusterClient redisClient,
            Long defaultDelayTimeSeconds,
            Boolean withConnectionPool) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
        this.withConnectionPool = withConnectionPool;
    }

    /**
     * Attempts to recover a value from the redis instance, using the provided key
     *
     * @param key key to be used in order to attempt the retreival of a value from redis
     * @return if found the String value obtained from redis, null otherwise
     */
    @Override
    public String get(String key) throws Exception {
        RedisClusterClient redisClient = (RedisClusterClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisClusterConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                return statefulConnection.sync().get(key);
            }
        } else {
            try (GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool =
                            ConnectionPoolSupport.createGenericObjectPool(
                                    redisClient::connect, new GenericObjectPoolConfig<>());
                    StatefulRedisClusterConnection<String, String> statefulConnection =
                            pool.borrowObject()) {
                return statefulConnection.sync().get(key);
            }
        }
    }

    /**
     * Saves the key-value pair in the Redis instance, using a default TTL
     *
     * @param key key to be used when saving the value
     * @param value value to be stored in the redis instance
     */
    @Override
    public void save(String key, String value) throws Exception {
        save(key, value, defaultDelayTimeSeconds);
    }

    /**
     * Saves asynchronously the key-value pair in the Redis instance, using the provided TTL
     *
     * @param key key to be used when saving the value
     * @param value value to be stored in the redis instance
     * @param delayTime seconds defining the stored data TTL
     */
    @Override
    public void save(String key, String value, Long delayTime) throws Exception {
        RedisClusterClient redisClient = (RedisClusterClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisClusterConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                executeSetMethod(key, value, delayTime, statefulConnection);
            }
        } else {
            GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool =
                    ConnectionPoolSupport.createGenericObjectPool(
                            redisClient::connect, new GenericObjectPoolConfig<>());
            StatefulRedisClusterConnection<String, String> statefulConnection = pool.borrowObject();
            executeSetMethod(key, value, delayTime, statefulConnection);
        }
    }

    /**
     * Deletes asynchronously a value from the Redis instance using the provided key
     *
     * @param key key to be used for content deletion from redis
     */
    @Override
    public void delete(String key) throws Exception {
        RedisClusterClient redisClient = (RedisClusterClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisClusterConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                executeAsyncDel(key, statefulConnection);
            }
        } else {
            GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool =
                    ConnectionPoolSupport.createGenericObjectPool(
                            redisClient::connect, new GenericObjectPoolConfig<>());
            StatefulRedisClusterConnection<String, String> statefulConnection = pool.borrowObject();
            executeAsyncDel(key, statefulConnection);
        }
    }

    private void executeAsyncDel(
            String key, StatefulRedisClusterConnection<String, String> statefulConnection) {
        statefulConnection
                .async()
                .del(key)
                .thenAccept(
                        result -> {
                            statefulConnection.closeAsync();
                        });
    }

    private void executeSetMethod(
            String key,
            String value,
            Long delayTime,
            StatefulRedisClusterConnection<String, String> statefulRedisConnection) {
        statefulRedisConnection
                .async()
                .set(key, String.valueOf(value))
                .thenAccept(
                        result -> {
                            if ("OK".equals(result)) {
                                statefulRedisConnection.async().expire(key, delayTime);
                            }
                            statefulRedisConnection.closeAsync();
                        });
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import lombok.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Getter
@Setter
/** Implements commands to a simple Redis instance using Lettuce */
public class SimpleRedisStorage extends RedisStorage {

    private Long defaultDelayTimeSeconds = 60L;
    private Boolean withConnectionPool = false;

    public SimpleRedisStorage(AbstractRedisClient redisClient) {
        super(redisClient);
    }

    public SimpleRedisStorage(RedisClient redisClient, Long defaultDelayTimeSeconds) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
    }

    public SimpleRedisStorage(
            RedisClient redisClient, Long defaultDelayTimeSeconds, Boolean withConnectionPool) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
        this.withConnectionPool = withConnectionPool;
    }

    /**
     * Retrieves from a synchronous connection the value stored in the Redis instance using the
     * provided key, closes the connection asynchronously
     *
     * @param key key to be used in order to attempt the retreival of a value from redis
     * @return value stored in the Redis instance
     */
    @Override
    public String get(String key) throws Exception {
        RedisClient redisClient = (RedisClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                return statefulConnection.sync().get(key);
            }
        } else {
            try (GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                            ConnectionPoolSupport.createGenericObjectPool(
                                    redisClient::connect, new GenericObjectPoolConfig<>());
                    StatefulRedisConnection<String, String> statefulConnection =
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

        RedisClient redisClient = (RedisClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                executeSetMethod(key, value, delayTime, statefulConnection);
            }
        } else {
            GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                    ConnectionPoolSupport.createGenericObjectPool(
                            redisClient::connect, new GenericObjectPoolConfig<>());
            StatefulRedisConnection<String, String> statefulConnection = pool.borrowObject();
            executeSetMethod(key, value, delayTime, statefulConnection);
        }
    }

    private void executeSetMethod(
            String key,
            String value,
            Long delayTime,
            StatefulRedisConnection<String, String> statefulRedisConnection) {
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

    /**
     * Deletes asynchronously a value from the Redis instance using the provided key
     *
     * @param key key to be used for content deletion from redis
     */
    @Override
    public void delete(String key) throws Exception {

        RedisClient redisClient = (RedisClient) getRedisClient();
        if (!withConnectionPool) {
            try (StatefulRedisConnection<String, String> statefulConnection =
                    redisClient.connect()) {
                executeAsyncDel(key, statefulConnection);
            }
        } else {
            GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                    ConnectionPoolSupport.createGenericObjectPool(
                            redisClient::connect, new GenericObjectPoolConfig<>());
            StatefulRedisConnection<String, String> statefulConnection = pool.borrowObject();
            executeAsyncDel(key, statefulConnection);
        }
    }

    private void executeAsyncDel(
            String key, StatefulRedisConnection<String, String> statefulConnection) {
        statefulConnection
                .async()
                .del(key)
                .thenAccept(
                        result -> {
                            statefulConnection.closeAsync();
                        });
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

/** Implements commands to a simple Redis instance using Lettuce */
public class SimpleRedisStorage extends RedisStorage {

    private Long defaultDelayTimeSeconds = 60L;

    public SimpleRedisStorage(AbstractRedisClient redisClient) {
        super(redisClient);
    }

    public SimpleRedisStorage(RedisClient redisClient, Long defaultDelayTimeSeconds) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
    }

    /**
     * Retrieves from a synchronous connection the value stored in the Redis instance using the
     * provided key, closes the connection asynchronously
     *
     * @param key key to be used in order to attempt the retreival of a value from redis
     * @return value stored in the Redis instance
     */
    @Override
    public String get(String key) {
        StatefulRedisConnection<String, String> statefulConnection =
                ((RedisClient) getRedisClient()).connect();
        try {
            return statefulConnection.sync().get(key);
        } finally {
            statefulConnection.closeAsync();
        }
    }

    /**
     * Saves the key-value pair in the Redis instance, using a default TTL
     *
     * @param key key to be used when saving the value
     * @param value value to be stored in the redis instance
     */
    @Override
    public void save(String key, String value) {
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
    public void save(String key, String value, Long delayTime) {
        StatefulRedisConnection<String, String> statefulRedisConnection =
                ((RedisClient) getRedisClient()).connect();
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
    public void delete(String key) {
        StatefulRedisConnection<String, String> statefulRedisConnection =
                ((RedisClient) getRedisClient()).connect();
        statefulRedisConnection
                .async()
                .del(key)
                .thenAccept(
                        result -> {
                            statefulRedisConnection.closeAsync();
                        });
    }
}

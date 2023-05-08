/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

/** Implements commands to a Redis Cluster using Lettuce */
public class ClusteredRedisStorage extends RedisStorage {

    private Long defaultDelayTimeSeconds = 60L;

    public ClusteredRedisStorage(RedisClusterClient redisClient) {
        super(redisClient);
    }

    public ClusteredRedisStorage(RedisClient redisClient, Long defaultDelayTimeSeconds) {
        super(redisClient);
        this.defaultDelayTimeSeconds = defaultDelayTimeSeconds;
    }

    /**
     * Attempts to recover a value from the redis instance, using the provided key
     *
     * @param key key to be used in order to attempt the retreival of a value from redis
     * @return if found the String value obtained from redis, null otherwise
     */
    @Override
    public String get(String key) {
        StatefulRedisClusterConnection<String, String> statefulConnection =
                ((RedisClusterClient) getRedisClient()).connect();
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
        StatefulRedisClusterConnection<String, String> statefulRedisConnection =
                ((RedisClusterClient) getRedisClient()).connect();
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
        StatefulRedisClusterConnection<String, String> statefulRedisConnection =
                ((RedisClusterClient) getRedisClient()).connect();
        statefulRedisConnection
                .async()
                .del(key)
                .thenAccept(
                        result -> {
                            statefulRedisConnection.closeAsync();
                        });
    }
}

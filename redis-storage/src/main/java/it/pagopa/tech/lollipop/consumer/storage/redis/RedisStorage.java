/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis;

import io.lettuce.core.AbstractRedisClient;
import lombok.Data;

/**
 * Abstract class to be used in order to define a redis storage using a Lettuce implementation of
 * the AbstractRedisClient
 */
@Data
public abstract class RedisStorage {

    private AbstractRedisClient redisClient;

    public RedisStorage(AbstractRedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * Attempts to recover a value from the redis instance, using the provided key
     *
     * @param key key to be used in order to attempt the retreival of a value from redis
     * @return if found the String value obtained from redis, null otherwise
     */
    public abstract String get(String key) throws Exception;

    /**
     * Saves a value in the redis using the provided key
     *
     * @param key key to be used when saving the value
     * @param value value to be stored in the redis instance
     */
    public abstract void save(String key, String value) throws Exception;

    /**
     * Saves a value in the redis using the provided key, with a defined TTL
     *
     * @param key key to be used when saving the value
     * @param value value to be stored in the redis instance
     * @param delayTime seconds defining the stored data TTL
     */
    public abstract void save(String key, String value, Long delayTime) throws Exception;

    /**
     * Deletes a value from redis using the provided key
     *
     * @param key key to be used for content deletion from redis
     */
    public abstract void delete(String key) throws Exception;
}

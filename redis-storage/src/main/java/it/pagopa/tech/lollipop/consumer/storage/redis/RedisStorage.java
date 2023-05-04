/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis;

import io.lettuce.core.AbstractRedisClient;
import lombok.Data;

@Data
public abstract class RedisStorage {

    private AbstractRedisClient redisClient;

    public RedisStorage(AbstractRedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public abstract Object get(String key);

    public abstract void save(String key, Object value);

    public abstract void delete(String key);
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

public class SimpleRedisStorage extends RedisStorage {

    public SimpleRedisStorage(RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    public Object get(String key) {

        StatefulRedisConnection<String, String> statefulConnection =
                ((RedisClient) getRedisClient()).connect();
        try {
            return statefulConnection.sync().get(key);
        } finally {
            statefulConnection.closeAsync();
        }
    }

    @Override
    public void save(String key, Object value) {
        StatefulRedisConnection<String, String> statefulRedisConnection =
                ((RedisClient) getRedisClient()).connect();
        ((RedisClient) getRedisClient())
                .connect()
                .async()
                .set(key, String.valueOf(value))
                .thenAccept(
                        result -> {
                            if ("OK".equals(result)) {
                                statefulRedisConnection.async().expire(key, 1000L);
                            }
                        });
    }

    @Override
    public void delete(String key) {
        ((RedisClient) getRedisClient()).connect().async().del(key);
    }
}

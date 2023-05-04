/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

public class ClusteredRedisStorage extends RedisStorage {

    public ClusteredRedisStorage(RedisClusterClient redisClient) {
        super(redisClient);
    }

    @Override
    public Object get(String key) {
        StatefulRedisClusterConnection<String, String> statefulConnection =
                ((RedisClusterClient) getRedisClient()).connect();
        try {
            return statefulConnection.sync().get(key);
        } finally {
            statefulConnection.closeAsync();
        }
    }

    @Override
    public void save(String key, Object value) {}

    @Override
    public void delete(String key) {}
}

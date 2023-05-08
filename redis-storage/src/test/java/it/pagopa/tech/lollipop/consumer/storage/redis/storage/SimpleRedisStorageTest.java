/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import it.pagopa.tech.lollipop.consumer.storage.redis.builder.DefaultRedisClientBuilder;
import it.pagopa.tech.lollipop.consumer.storage.redis.config.RedisStorageConfig;
import java.io.IOException;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import redis.embedded.RedisSentinel;
import redis.embedded.RedisServer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleRedisStorageTest {

    private String bindAddress;

    private RedisServer redisServer;
    private RedisSentinel redisSentinel;
    private SimpleRedisStorage redisStorage;

    private SimpleRedisStorage sentinelRedisStoragere;

    @BeforeAll
    public void setUp() throws IOException {

        redisServer = RedisServer.newRedisServer().build();
        redisSentinel = RedisSentinel.newRedisSentinel().build();
        redisServer.start();
        redisSentinel.start();
        redisStorage =
                (SimpleRedisStorage)
                        new DefaultRedisClientBuilder(
                                        RedisStorageConfig.builder()
                                                .mainNode(
                                                        RedisStorageConfig.RedisNode.builder()
                                                                .port(6379)
                                                                .build())
                                                .build())
                                .createStorage();
        sentinelRedisStoragere =
                (SimpleRedisStorage)
                        new DefaultRedisClientBuilder(
                                        RedisStorageConfig.builder()
                                                .withSentinel(true)
                                                .masterIds(Collections.singletonList("mymaster"))
                                                .mainNode(
                                                        RedisStorageConfig.RedisNode.builder()
                                                                .port(26379)
                                                                .build())
                                                .build())
                                .createStorage();
    }

    @SneakyThrows
    @Test
    public void successfulOperationsOnRedisStorage() {
        redisStorage.save("test-key", "test");
        Thread.sleep(30);
        String result = redisStorage.get("test-key");
        Assertions.assertEquals("test", result);
        redisStorage.delete("test-key");
        Thread.sleep(30);
        result = redisStorage.get("test-key");
        Assertions.assertNull(result);
    }

    @SneakyThrows
    @Test
    public void successfulEvictOps() {
        redisStorage.save("test-key", "test", 1L);
        Thread.sleep(100);
        String result = redisStorage.get("test-key");
        Assertions.assertEquals("test", result);
        Thread.sleep(1100);
        result = redisStorage.get("test-key");
        Assertions.assertNull(result);
    }

    @SneakyThrows
    @Test
    public void successfulOperationsOnRedisStorageWithConnectionPool() {
        redisStorage.setWithConnectionPool(true);
        redisStorage.save("test-key", "test");
        Thread.sleep(100);
        String result = redisStorage.get("test-key");
        Assertions.assertEquals("test", result);
        redisStorage.delete("test-key");
        Thread.sleep(30);
        result = redisStorage.get("test-key");
        Assertions.assertNull(result);
    }

    @SneakyThrows
    @Test
    public void successfulEvictOpsWithConnectionPool() {
        redisStorage.setWithConnectionPool(true);
        redisStorage.save("test-key", "test", 1L);
        Thread.sleep(100);
        String result = redisStorage.get("test-key");
        Assertions.assertEquals("test", result);
        Thread.sleep(1100);
        result = redisStorage.get("test-key");
        Assertions.assertNull(result);
    }

    @SneakyThrows
    @Test
    public void successfulOperationsOnRedisStorageWithConnectionPoolAndSentinel() {
        sentinelRedisStoragere.setWithConnectionPool(true);
        sentinelRedisStoragere.save("test-key", "test");
        Thread.sleep(100);
        String result = sentinelRedisStoragere.get("test-key");
        Assertions.assertEquals("test", result);
        sentinelRedisStoragere.delete("test-key");
        Thread.sleep(30);
        result = sentinelRedisStoragere.get("test-key");
        Assertions.assertNull(result);
    }

    @SneakyThrows
    @Test
    public void successfulEvictOpsWithConnectionPoolAndSentinel() {
        sentinelRedisStoragere.setWithConnectionPool(true);
        sentinelRedisStoragere.save("test-key", "test", 1L);
        Thread.sleep(100);
        String result = sentinelRedisStoragere.get("test-key");
        Assertions.assertEquals("test", result);
        Thread.sleep(1200);
        result = sentinelRedisStoragere.get("test-key");
        Assertions.assertNull(result);
    }

    @AfterAll
    public void tearDown() throws IOException {
        redisStorage.getRedisClient().close();
        sentinelRedisStoragere.getRedisClient().close();
        redisServer.stop();
        redisSentinel.stop();
    }
}

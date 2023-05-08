/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import it.pagopa.tech.lollipop.consumer.storage.redis.builder.DefaultRedisClientBuilder;
import it.pagopa.tech.lollipop.consumer.storage.redis.config.RedisStorageConfig;
import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import redis.embedded.RedisServer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleRedisStorageTest {

    private RedisServer redisServer;
    private SimpleRedisStorage redisStorage;

    @BeforeAll
    public void setUp() throws IOException {
        redisServer = RedisServer.newRedisServer().port(6999).build();
        redisServer.start();
        redisStorage =
                (SimpleRedisStorage)
                        new DefaultRedisClientBuilder(
                                        RedisStorageConfig.builder()
                                                .mainNode(
                                                        RedisStorageConfig.RedisNode.builder()
                                                                .port(6999)
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
        Thread.sleep(30);
        String result = redisStorage.get("test-key");
        Assertions.assertEquals("test", result);
        Thread.sleep(1000);
        result = redisStorage.get("test-key");
        Assertions.assertNull(result);
    }

    @AfterAll
    public void tearDown() throws IOException {
        redisStorage.getRedisClient().close();
        redisServer.stop();
    }
}

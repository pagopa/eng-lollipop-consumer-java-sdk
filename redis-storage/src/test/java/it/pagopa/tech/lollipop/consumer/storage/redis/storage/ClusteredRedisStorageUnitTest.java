/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.storage;

import static org.assertj.core.api.Assertions.assertThatNoException;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.protocol.AsyncCommand;
import it.pagopa.tech.lollipop.consumer.storage.redis.builder.DefaultRedisClientBuilder;
import it.pagopa.tech.lollipop.consumer.storage.redis.config.RedisStorageConfig;
import java.io.IOException;
import java.util.ArrayList;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClusteredRedisStorageUnitTest {

    private ClusteredRedisStorage redisStorage;

    RedisClusterClient redisClusterClient;

    StatefulRedisClusterConnection<String, String> redisClusterConnection;

    RedisAdvancedClusterAsyncCommands<String, String> redisAdvancedClusterAsyncCommands;

    RedisAdvancedClusterCommands<String, String> redisAdvancedClusterCommands;

    @BeforeAll
    public void setUp() throws IOException {

        redisStorage =
                (ClusteredRedisStorage)
                        new DefaultRedisClientBuilder(
                                        RedisStorageConfig.builder()
                                                .clusterConnection(true)
                                                .mainNode(
                                                        RedisStorageConfig.RedisNode.builder()
                                                                .port(6669)
                                                                .build())
                                                .clusterNodeList(new ArrayList<>())
                                                .build())
                                .createStorage();
        redisClusterClient = Mockito.mock(RedisClusterClient.class);
        redisClusterConnection = Mockito.mock(StatefulRedisClusterConnection.class);
        redisAdvancedClusterAsyncCommands = Mockito.mock(RedisAdvancedClusterAsyncCommands.class);
        redisAdvancedClusterCommands = Mockito.mock(RedisAdvancedClusterCommands.class);

        Mockito.when(redisClusterClient.connect()).thenReturn(redisClusterConnection);
        Mockito.when(redisClusterConnection.async()).thenReturn(redisAdvancedClusterAsyncCommands);
        Mockito.when(redisClusterConnection.sync()).thenReturn(redisAdvancedClusterCommands);

        Mockito.when(redisAdvancedClusterAsyncCommands.set(Mockito.any(), Mockito.any()))
                .thenReturn(Mockito.mock(AsyncCommand.class));
        Mockito.when(redisAdvancedClusterAsyncCommands.del(Mockito.any()))
                .thenReturn(Mockito.mock(AsyncCommand.class));
        Mockito.when(redisAdvancedClusterCommands.get(Mockito.any())).thenReturn("test");

        redisStorage.setRedisClient(redisClusterClient);
    }

    @SneakyThrows
    @Test
    public void testThatCallsSave() {

        assertThatNoException().isThrownBy(() -> redisStorage.save("test-key", "test"));
    }

    @SneakyThrows
    @Test
    public void testThatCallsGet() {

        assertThatNoException().isThrownBy(() -> redisStorage.get("test-key"));
    }

    @SneakyThrows
    @Test
    public void testThatCallsDelete() {
        assertThatNoException().isThrownBy(() -> redisStorage.delete("test-key"));
    }

    @AfterAll
    public void wrapDown() throws IOException {
        redisStorage.getRedisClient().close();
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisStorageConfig {

    @Builder.Default private RedisNode mainNode = new RedisNode();
    @Builder.Default private boolean withAuth = false;
    @Builder.Default private String username = "username";
    @Builder.Default private String password = "password";
    @Builder.Default private boolean clusterConnection = false;
    @Builder.Default private boolean withSsl = false;
    @Builder.Default private String clientName = "LettuceClient";
    @Builder.Default private Long defaultDelay = 60L;
    @Builder.Default private boolean withConnectionPooling = false;
    @Builder.Default private boolean withSentinel = false;
    private List<RedisNode> clusterNodeList;
    private List<String> sentinelHostList;
    private List<String> masterIds;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class RedisNode {

        @Builder.Default private String hostname = "localhost";
        @Builder.Default private Integer port = 6379;
    }
}

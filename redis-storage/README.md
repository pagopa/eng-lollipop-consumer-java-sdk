# Lollipop SDK Redis Storage

This module contains implementations of the storage interfaces using a Redis Client with the Lettuce implementation.

## Configuration

The following tables contains the properties of the [RedisStorageConfig](redis-storage/src/main/java/it/pagopa/tech/lollipop/consumer/storage/redis/config/RedisStorageConfig.java)
configuration class, to be used in order to statup a Redis Client within the module.

| VARIABLE              | DEFAULT VALUE                         | USAGE                                                                                                                                                                                                      |
|-----------------------|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| mainNode              | {"hostmane":"localhost","port": 6379} | RedisNode model containing a hostname and port to be used. If not in sentinel mode this is the mandatory main references to a Redis instance, otherwise valid Sentinel node host and port must be provided |
| withAuth              | false                                 | Flag to indicate if the connection requires authentication                                                                                                                                                 |
| username              | username                              | Username to be used if authentication is required                                                                                                                                                          |
| password              | password                              | Password to be used if authentication is required                                                                                                                                                          |
| clusterConnection     | false                                 | Flag to determine if the client will attempt to work in clustered mode                                                                                                                                     |
| withSsl               | false                                 | Flag to determine if the client requires an ssl connection                                                                                                                                                 |
| clientName            | LettuceClient                         | Name of the client                                                                                                                                                                                         |
| defaultDelay          | 60                                    | The default eviction delay (in seconds)                                                                                                                                                                    |
| withConnectionPooling | false                                 | Flag to indicate if the connection with work with a connection pool                                                                                                                                        |
| withSentinel          | false                                 | Flag to indicate if the client will attempt to connect in sentinel mode for HA                                                                                                                             |
| clusterNodeList       | -                                     | List of RedisNode data structures (json containing hostname and port), to be used for extra nodes in cluster mode                                                                                          |
| sentinelHostList      | -                                     | List of sentinel hosts, to be used if in sentinel mode                                                                                                                                                     |
| masterIds             | -                                     | Lost of masterIds to be used if in sentinel mode                                                                                                                                                           |


## Examples

### Simple

The following snippet provides a basic sample in order to use the implementation of the storage interfaces

```
RedisStorageConfig redisConfig = RedisStorageConfig.builder().mainNode(
                        RedisStorageConfig.RedisNode
                            .builder()
                            .hostname("redisnode1")
                            .port(6379)
                            .build());
DefaultClientBuilder builder = new DefaultRedisClientBuilder(redisConfig);
RedisStorageProvisioner redisStoreProvisioner = new RedisStorageProvisioner(builder.build());
idpRedisStorage = new RedisIdpCertStorageProvider(redisStoreProvisioner).provideStorage(idpCertStorageConfig);
assertionRedisStorage = new RedisAssertionStorageProvider(redisStoreProvisioner).provideStorage(assertionStorageConfig);

```

In order to execute with connection pooling active, provide a redisConfig as for the following example:

### Pooling startup

```
RedisStorageConfig redisConfig = RedisStorageConfig.builder()
                        .withConnectionPooling(true)
                        .mainNode(
                            RedisStorageConfig.RedisNode
                                .builder()
                                .hostname("redisnode1")
                                .port(6379)
                                .build()
                        );
```

### With Sentinel nodes

In order to execute with sentinel active, provide a redisConfig as for the following example, defining a simple
configuration with a main node, an extra sentinel host, and two master nodes, in connection pooling mode:

```
RedisStorageConfig redisConfig = RedisStorageConfig.builder()
                        .withConnectionPooling(true)
                        .withSentinel(true)
                        .sentinelHostList(Arrays.asList("sentinel2"))
                        .masterIds(Arrays.asList("master1","master2"))
                        .mainNode(
                            RedisStorageConfig.RedisNode
                                .builder()
                                .hostname("sentinel1")
                                .port(26379)
                                .build()
                        );
```

### Clustered Connections

In order to execute in clustered mode, one or more nodes must be provided, as follows:
```
RedisStorageConfig redisConfig = RedisStorageConfig.builder()
                        .clusterConnection(true)
                        .mainNode(
                            RedisStorageConfig.RedisNode
                                .builder()
                                .hostname("redisnode1")
                                .port(6379)
                                .build()
                        )
                        .clusterNodeList(Arrays.asList(
                            RedisStorageConfig.RedisNode
                                .builder()
                                .hostname("redisnode2")
                                .port(6379)
                                .build()
                        ));
```
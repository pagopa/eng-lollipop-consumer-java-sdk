/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.builder;

import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;

public interface ClientBuilder {

    RedisStorage createStorage();
}

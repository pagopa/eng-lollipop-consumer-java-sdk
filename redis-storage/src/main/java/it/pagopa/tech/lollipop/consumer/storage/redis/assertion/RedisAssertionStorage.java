/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.assertion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.openapitools.jackson.nullable.JsonNullableModule;

/**
 * Implementation of the {@link AssertionStorage} interface as a redis storage.
 *
 * <p>The storage can be configured via the {@link StorageConfig} configuration class.
 */
public class RedisAssertionStorage implements AssertionStorage {

    private final RedisStorage redisStorage;
    private final StorageConfig storageConfig;

    private final ObjectMapper objectMapper;

    public RedisAssertionStorage(RedisStorage redisStorage, StorageConfig storageConfig) {
        this.redisStorage = redisStorage;
        this.storageConfig = storageConfig;
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new JsonNullableModule());
    }

    /**
     * Retrieve the SAML Assertion associated with the provided assertionRef if the storage is
     * enabled {@link IdpCertStorageConfig}, otherwise no operation is performed.
     *
     * @param ref the assertion reference to be used as key
     * @return the SAML Assertion if found, null if no data is present in the storage or the storage
     *     is disabled
     */
    @SneakyThrows
    @Override
    public SamlAssertion getAssertion(String ref) {
        if (!storageConfig.isAssertionStorageEnabled()) {
            return null;
        }

        String objString = redisStorage.get(ref);

        return objString == null ? null : objectMapper.readValue(objString, SamlAssertion.class);
    }

    /**
     * Store the SAML Assertion if the storage is enabled {@link StorageConfig}, otherwise no
     * operation is performed.
     *
     * <p>Once the SAML Assertion is stored an eviction operation is scheduled with a delay
     * configured via {@link AssertionStorage}
     *
     * @param assertionRef the assertion Ref
     * @param samlAssertion SAML Assertion instance
     */
    @SneakyThrows
    @Override
    public void saveAssertion(String assertionRef, SamlAssertion samlAssertion) {
        if (!storageConfig.isAssertionStorageEnabled()) {
            return;
        }

        redisStorage.save(
                assertionRef,
                objectMapper.writeValueAsString(samlAssertion),
                TimeUnit.SECONDS.convert(
                        storageConfig.getStorageEvictionDelay(),
                        storageConfig.getStorageEvictionDelayTimeUnit()));
    }
}

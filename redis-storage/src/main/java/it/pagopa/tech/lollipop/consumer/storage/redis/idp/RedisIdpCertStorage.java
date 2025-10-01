/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.storage.redis.idp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.storage.redis.RedisStorage;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.openapitools.jackson.nullable.JsonNullableModule;

/**
 * Implementation of the {@link IdpCertStorage} interface as a redis storage.
 *
 * <p>The storage can be configured via the {@link IdpCertStorageConfig} configuration class.
 */
public class RedisIdpCertStorage implements IdpCertStorage {

    private final RedisStorage redisStorage;
    private final IdpCertStorageConfig storageConfig;

    private ObjectMapper objectMapper;

    public RedisIdpCertStorage(
            RedisStorage redisStorage, IdpCertStorageConfig idpCertStorageConfig) {
        this.redisStorage = redisStorage;
        this.storageConfig = idpCertStorageConfig;
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
     * Retrieve the idpCertData associated with the provided tag if the storage is enabled {@link
     * IdpCertStorageConfig}, otherwise no operation is performed.
     *
     * @param tag the idpCertData issue instant
     * @return the list of cert data if found, null if no cert data are present in the storage or
     *     the storage is disabled
     */
    @SneakyThrows
    @Override
    public IdpCertData getIdpCertData(String tag) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return null;
        }

        String objString = redisStorage.get(tag);

        return objString == null ? null : objectMapper.readValue(objString, IdpCertData.class);
    }

    /**
     * Store the idpCertData if the storage is enabled {@link IdpCertStorageConfig}, otherwise no
     * operation is performed.
     *
     * <p>Once the idpCertData is stored an eviction operation is scheduled with a delay configured
     * via {@link IdpCertStorageConfig}
     *
     * @param tag the idpCertData issue instant
     * @param idpCertData
     */
    @SneakyThrows
    @Override
    public void saveIdpCertData(String tag, IdpCertData idpCertData) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return;
        }

        redisStorage.save(
                tag,
                objectMapper.writeValueAsString(idpCertData),
                TimeUnit.SECONDS.convert(
                        storageConfig.getStorageEvictionDelay(),
                        storageConfig.getStorageEvictionDelayTimeUnit()));
    }
}

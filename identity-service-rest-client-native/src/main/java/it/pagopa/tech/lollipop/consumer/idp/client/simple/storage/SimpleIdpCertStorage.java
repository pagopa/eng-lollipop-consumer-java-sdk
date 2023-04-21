/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.storage;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.inject.Inject;

/**
 * Implementation of the {@link IdpCertStorage} interface as a simple in memory storage.
 *
 * <p>The storage can be configured via the {@link IdpCertStorageConfig} configuration class.
 *
 * <p>It store in a in memory {@link java.util.HashMap} the assertions and the associated scheduled
 * eviction operations, every time an assertion is accessed the associated eviction operation is
 * rescheduled.
 */
public class SimpleIdpCertStorage implements IdpCertStorage {

    private final Map<String, IdpCertData> idpCertDataMap;
    private final Map<String, ScheduledFuture<?>> scheduledEvictionsMap;
    private final IdpCertStorageConfig storageConfig;

    @Inject
    public SimpleIdpCertStorage(
            Map<String, IdpCertData> idpCertDataMap,
            Map<String, ScheduledFuture<?>> scheduledEvictionsMap,
            IdpCertStorageConfig storageConfig) {
        this.idpCertDataMap = idpCertDataMap;
        this.scheduledEvictionsMap = scheduledEvictionsMap;
        this.storageConfig = storageConfig;
    }

    /**
     * Retrieve the idpCertData associated with the provided tag if the storage is enabled {@link
     * IdpCertStorageConfig}, otherwise no operation is performed.
     *
     * <p>Before the list of idpCertData is returned the associated eviction operation is
     * rescheduled with the delay configured via {@link IdpCertStorageConfig}
     *
     * @param tag the idpCertData issue instant
     * @return the list of cert data if found, null if no cert data are present in the storage or
     *     the storage is disabled
     */
    @Override
    public IdpCertData getIdpCertData(String tag) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return null;
        }

        IdpCertData idpCertData = idpCertDataMap.get(tag);
        if (idpCertData != null) {
            delayEviction(tag);
        }
        return idpCertData;
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
    @Override
    public void saveIdpCertData(String tag, IdpCertData idpCertData) {
        if (!storageConfig.isIdpCertDataStorageEnabled()) {
            return;
        }

        idpCertDataMap.put(tag, idpCertData);
        scheduleEviction(tag);
    }

    private void scheduleEviction(String assertionRef) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> schedule =
                executorService.schedule(
                        getEvictionTask(assertionRef),
                        storageConfig.getStorageEvictionDelay(),
                        storageConfig.getStorageEvictionDelayTimeUnit());
        scheduledEvictionsMap.put(assertionRef, schedule);
    }

    private void delayEviction(String tag) {
        ScheduledFuture<?> schedule = scheduledEvictionsMap.get(tag);
        schedule.cancel(false);
        scheduledEvictionsMap.remove(tag);
        scheduleEviction(tag);
    }

    private Runnable getEvictionTask(String tag) {
        return () -> {
            idpCertDataMap.remove(tag);
            scheduledEvictionsMap.remove(tag);
        };
    }
}

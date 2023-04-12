/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.inject.Inject;

/**
 * Implementation of the {@link AssertionStorage} interface as a simple in memory storage.
 *
 * <p>The storage can be configured via the {@link StorageConfig} configuration class.
 *
 * <p>It store in a in memory {@link java.util.HashMap} the assertions and the associated scheduled
 * eviction operations, every time an assertion is accessed the associated eviction operation is
 * rescheduled.
 */
public class SimpleAssertionStorage implements AssertionStorage {

    private final Map<String, SamlAssertion> assertionMap;
    private final Map<String, ScheduledFuture<?>> scheduledEvictionsMap;
    private final StorageConfig storageConfig;

    @Inject
    public SimpleAssertionStorage(
            Map<String, SamlAssertion> assertionMap,
            Map<String, ScheduledFuture<?>> scheduledEvictionsMap,
            StorageConfig storageConfig) {
        this.assertionMap = assertionMap;
        this.scheduledEvictionsMap = scheduledEvictionsMap;
        this.storageConfig = storageConfig;
    }

    /**
     * Retrieve the assertion associated with the provided assertion reference if the storage is
     * enabled {@link StorageConfig}, otherwise no operation is performed.
     *
     * <p>Before the assertion is returned the associated eviction operation is rescheduled with the
     * delay configured via {@link StorageConfig}
     *
     * @param assertionRef the assertion reference
     * @return the SAML assertion if found, null if the assertion is not present in the storage or
     *     the storage is disabled
     */
    @Override
    public SamlAssertion getAssertion(String assertionRef) {
        if (!storageConfig.isAssertionStorageEnabled()) {
            return null;
        }

        SamlAssertion samlAssertion = assertionMap.get(assertionRef);
        if (samlAssertion != null) {
            delayEviction(assertionRef);
        }
        return samlAssertion;
    }

    /**
     * Store the assertion if the storage is enabled {@link StorageConfig}, otherwise no operation
     * is performed.
     *
     * <p>Once the assertion is stored an eviction operation is scheduled with a delay configured
     * via {@link StorageConfig}
     *
     * @param assertionRef the assertion reference
     * @param assertion the SAML assertion
     */
    @Override
    public void saveAssertion(String assertionRef, SamlAssertion assertion) {
        if (!storageConfig.isAssertionStorageEnabled()) {
            return;
        }

        assertionMap.put(assertionRef, assertion);
        scheduleEviction(assertionRef);
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

    private void delayEviction(String assertionRef) {
        ScheduledFuture<?> schedule = scheduledEvictionsMap.get(assertionRef);
        schedule.cancel(false);
        scheduledEvictionsMap.remove(assertionRef);
        scheduleEviction(assertionRef);
    }

    private Runnable getEvictionTask(String assertionRef) {
        return () -> {
            assertionMap.remove(assertionRef);
            scheduledEvictionsMap.remove(assertionRef);
        };
    }
}

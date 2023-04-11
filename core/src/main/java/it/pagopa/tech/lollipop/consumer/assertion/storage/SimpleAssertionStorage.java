/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/** Implementation of the {@link AssertionStorage} interface as a simple in memory storage */
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
     * Retrieve the assertion associated with the provided assertion reference
     *
     * @param assertionRef the assertion reference
     * @return the SAML assertion if found, otherwise null
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
     * Store the assertion
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
        ScheduledFuture<?> schedule = executorService.schedule(getEvictionTask(assertionRef), storageConfig.getStorageEvictionDelay(), storageConfig.getStorageEvictionDelayTimeUnit());
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

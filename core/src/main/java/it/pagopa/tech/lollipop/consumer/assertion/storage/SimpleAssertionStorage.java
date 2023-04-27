/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.DelayedCacheObject;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final ConcurrentHashMap<String, SoftReference<SamlAssertion>> cache;
    private final DelayQueue<DelayedCacheObject<SamlAssertion>> cleaningUpQueue;
    private Thread cleanerThread;

    private AtomicInteger numberOfElements;
    private final StorageConfig storageConfig;

    private Executor executor;

    @Inject
    public SimpleAssertionStorage(StorageConfig storageConfig) {
        cache = new ConcurrentHashMap<>();
        cleaningUpQueue = new DelayQueue<>();
        initCleanerThread();
        this.storageConfig = storageConfig;
        executor = new ScheduledThreadPoolExecutor(1);
        numberOfElements = new AtomicInteger(0);
    }

    @Inject
    protected SimpleAssertionStorage(
            ConcurrentHashMap<String, SoftReference<SamlAssertion>> concurrentHashMap,
            DelayQueue<DelayedCacheObject<SamlAssertion>> queue,
            StorageConfig storageConfig) {
        cache = concurrentHashMap;
        cleaningUpQueue = queue;
        initCleanerThread();
        this.storageConfig = storageConfig;
        executor = new ScheduledThreadPoolExecutor(1);
        numberOfElements = new AtomicInteger(0);
    }

    private void initCleanerThread() {
        this.cleanerThread =
                new Thread(
                        () -> {
                            DelayedCacheObject<SamlAssertion> delayedCacheObject;
                            while (!Thread.currentThread().isInterrupted()) {
                                try {
                                    delayedCacheObject = this.cleaningUpQueue.take();
                                    this.cache.remove(
                                            delayedCacheObject.getKey(),
                                            delayedCacheObject.getReference());
                                    CompletableFuture.supplyAsync(
                                            () -> {
                                                numberOfElements.decrementAndGet();
                                                return true;
                                            },
                                            executor);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        });
        this.cleanerThread.setDaemon(true);
        this.cleanerThread.start();
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

        SamlAssertion samlAssertion =
                Optional.ofNullable(cache.get(assertionRef)).map(SoftReference::get).orElse(null);

        if (samlAssertion != null) {
            CompletableFuture.supplyAsync(
                    () -> {
                        cleaningUpQueue.removeIf(
                                cacheObject ->
                                        cacheObject
                                                .getKey()
                                                .equals(samlAssertion.getAssertionRef()));
                        numberOfElements.decrementAndGet();
                        saveAssertion(samlAssertion.getAssertionRef(), samlAssertion);
                        return true;
                    },
                    executor);
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

        if (assertionRef == null) {
            return;
        }
        if (assertion == null) {
            cache.remove(assertionRef);
        } else {
            CompletableFuture.supplyAsync(
                    () -> {
                        if (numberOfElements.get() >= storageConfig.getMaxNumberOfElements()) {
                            cleaningUpQueue.remove();
                        }
                        long expiryTime =
                                System.currentTimeMillis()
                                        + TimeUnit.MILLISECONDS.convert(
                                                storageConfig.getStorageEvictionDelay(),
                                                storageConfig.getStorageEvictionDelayTimeUnit());
                        SoftReference<SamlAssertion> reference = new SoftReference<>(assertion);
                        cache.put(assertionRef, reference);
                        cleaningUpQueue.put(
                                new DelayedCacheObject<>(assertionRef, reference, expiryTime));
                        numberOfElements.incrementAndGet();
                        return true;
                    },
                    executor);
        }
    }

    public void close() {
        this.cleanerThread.interrupt();
        this.cleaningUpQueue.clear();
        this.cache.clear();
    }
}

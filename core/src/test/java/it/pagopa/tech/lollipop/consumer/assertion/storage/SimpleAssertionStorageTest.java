/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import it.pagopa.tech.lollipop.consumer.model.DelayedCacheObject;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.lang.ref.SoftReference;
import java.util.concurrent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleAssertionStorageTest {

    private static final long EVICTION_DELAY = 5000L;
    private static StorageConfig storageConfigMock;
    private AssertionStorage sut;
    private static final String ASSERTION_REF_1 = "assertionRef1";

    @BeforeEach
    void setUp() {
        storageConfigMock = mock(StorageConfig.class);
        doReturn(EVICTION_DELAY).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
    }

    @Test
    void getNotExistingAssertionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        sut = new SimpleAssertionStorage(storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);
    }

    @Test
    void saveAssertionAndScheduleEvictionWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
        doReturn(100L).when(storageConfigMock).getMaxNumberOfElements();

        ConcurrentHashMap<String, SoftReference<SamlAssertion>> assertionMap =
                new ConcurrentHashMap<>();
        DelayQueue<DelayedCacheObject<SamlAssertion>> delayedCacheObjects = new DelayQueue<>();

        sut = new SimpleAssertionStorage(assertionMap, delayedCacheObjects, storageConfigMock);
        SamlAssertion samlAssertion = new SamlAssertion();

        sut.saveAssertion(ASSERTION_REF_1, samlAssertion);
        delayedCacheObjects.poll(100, TimeUnit.MILLISECONDS);

        assertEquals(1, assertionMap.size());
        assertEquals(1, delayedCacheObjects.size());
        assertEquals(samlAssertion, assertionMap.get(ASSERTION_REF_1).get());

        delayedCacheObjects.poll(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, assertionMap.size());
        assertEquals(0, delayedCacheObjects.size());
    }

    @Test
    void saveAssertionToMaximumCapacityWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
        doReturn(100L).when(storageConfigMock).getMaxNumberOfElements();

        ConcurrentHashMap<String, SoftReference<SamlAssertion>> assertionMap =
                new ConcurrentHashMap<>();
        DelayQueue<DelayedCacheObject<SamlAssertion>> delayedCacheObjects = new DelayQueue<>();

        sut = new SimpleAssertionStorage(assertionMap, delayedCacheObjects, storageConfigMock);
        SamlAssertion samlAssertion = new SamlAssertion();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 101; i++) {
            executor.submit(() -> sut.saveAssertion(ASSERTION_REF_1, samlAssertion));
        }
        delayedCacheObjects.poll(100, TimeUnit.MILLISECONDS);

        assertEquals(100, delayedCacheObjects.size());
        assertEquals(samlAssertion, assertionMap.get(ASSERTION_REF_1).get());

        //        delayedCacheObjects.poll(1000, TimeUnit.MILLISECONDS);
        //        assertEquals(0, assertionMap.size());
        //        assertEquals(0, delayedCacheObjects.size());
    }

    @Test
    void getAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();

        sut = new SimpleAssertionStorage(storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);
    }

    @Test
    void savaAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isAssertionStorageEnabled();

        ConcurrentHashMap<String, SoftReference<SamlAssertion>> assertionMap =
                new ConcurrentHashMap<>();
        DelayQueue<DelayedCacheObject<SamlAssertion>> delayedCacheObjects = new DelayQueue<>();

        sut = new SimpleAssertionStorage(assertionMap, delayedCacheObjects, storageConfigMock);

        sut.saveAssertion(ASSERTION_REF_1, new SamlAssertion());

        assertEquals(0, assertionMap.size());
        assertEquals(0, delayedCacheObjects.size());
    }
}

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
    private SimpleAssertionStorage sut;
    private static final String ASSERTION_REF_1 = "assertionRef1";

    @BeforeEach
    void setUp() {
        storageConfigMock = mock(StorageConfig.class);
        doReturn(EVICTION_DELAY).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
    }

    @Test
    void getExistingAssertionAndResetScheduleEvictionWithStorageEnabled()
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
        delayedCacheObjects.poll(20, TimeUnit.MILLISECONDS);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNotNull(result);
        assertEquals(samlAssertion, result);
        delayedCacheObjects.poll(20, TimeUnit.MILLISECONDS);
        assertEquals(1, delayedCacheObjects.size());

        delayedCacheObjects.poll(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, assertionMap.size());
        assertEquals(0, delayedCacheObjects.size());

        sut.close();
    }

    @Test
    void getNotExistingAssertionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        sut = new SimpleAssertionStorage(storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);

        sut.close();
    }

    // TODO
    // @Test
    // void saveAssertionAndScheduleEvictionWithStorageEnabled()
    //         throws InterruptedException, ExecutionException {
    //     doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();
    //     doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
    //
    // doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
    //     doReturn(100L).when(storageConfigMock).getMaxNumberOfElements();

    //     ConcurrentHashMap<String, SoftReference<SamlAssertion>> assertionMap =
    //             new ConcurrentHashMap<>();
    //     DelayQueue<DelayedCacheObject<SamlAssertion>> delayedCacheObjects = new DelayQueue<>();

    //     sut = new SimpleAssertionStorage(assertionMap, delayedCacheObjects, storageConfigMock);
    //     SamlAssertion samlAssertion = new SamlAssertion();

    //     sut.saveAssertion(ASSERTION_REF_1, samlAssertion);
    //     delayedCacheObjects.poll(100, TimeUnit.MILLISECONDS);

    //     assertEquals(1, assertionMap.size());
    //     assertEquals(1, delayedCacheObjects.size());
    //     assertEquals(samlAssertion, assertionMap.get(ASSERTION_REF_1).get());

    //     delayedCacheObjects.poll(1100, TimeUnit.MILLISECONDS);
    //     assertEquals(0, assertionMap.size());
    //     assertEquals(0, delayedCacheObjects.size());

    //     sut.close();
    // }

    @Test
    void saveAssertionToMaximumCapacityWithStorageEnabled() throws InterruptedException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
        doReturn(100L).when(storageConfigMock).getMaxNumberOfElements();

        ConcurrentHashMap<String, SoftReference<SamlAssertion>> assertionMap =
                new ConcurrentHashMap<>();
        DelayQueue<DelayedCacheObject<SamlAssertion>> delayedCacheObjects = new DelayQueue<>();

        sut = new SimpleAssertionStorage(assertionMap, delayedCacheObjects, storageConfigMock);
        SamlAssertion samlAssertion;

        for (int i = 0; i < 120; i++) {
            samlAssertion = new SamlAssertion();
            samlAssertion.setAssertionRef(ASSERTION_REF_1 + i);
            sut.saveAssertion(ASSERTION_REF_1 + i, samlAssertion);
        }
        delayedCacheObjects.poll(200, TimeUnit.MILLISECONDS);

        assertEquals(100, delayedCacheObjects.size());

        sut.close();
    }

    @Test
    void getAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(1000L).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();

        sut = new SimpleAssertionStorage(storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);

        sut.close();
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

        sut.close();
    }

    @Test
    void removedCacheObjectIfNotNull() throws InterruptedException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();
        doReturn(10000L).when(storageConfigMock).getStorageEvictionDelay();
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

        sut.removeDelayedObject();

        assertEquals(0, assertionMap.size());
        assertEquals(0, delayedCacheObjects.size());

        sut.close();
    }
}

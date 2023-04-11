/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import lombok.SneakyThrows;
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
    void getExistingAssertionAndResetScheduleEvictionWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        Map<String, SamlAssertion> assertionMap = new HashMap<>();
        SamlAssertion samlAssertion = new SamlAssertion();
        assertionMap.put(ASSERTION_REF_1, samlAssertion);
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();
        ScheduledFuture<?> scheduledFutureMock = mock(ScheduledFuture.class);
        scheduledEvictionsMap.put(ASSERTION_REF_1, scheduledFutureMock);

        sut = new SimpleAssertionStorage(assertionMap, scheduledEvictionsMap, storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNotNull(result);
        assertEquals(samlAssertion, result);
        assertEquals(1, scheduledEvictionsMap.size());

        CompletableFuture<Boolean> future = waitEvictionEnd(scheduledEvictionsMap);
        assertEquals(true, future.get());
        assertEquals(0, assertionMap.size());
        assertEquals(0, scheduledEvictionsMap.size());
    }

    @Test
    void getNotExistingAssertionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        sut = new SimpleAssertionStorage(new HashMap<>(), new HashMap<>(), storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);
    }

    @Test
    void saveAssertionAndScheduleEvictionWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        Map<String, SamlAssertion> assertionMap = new HashMap<>();
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();

        sut = new SimpleAssertionStorage(assertionMap, scheduledEvictionsMap, storageConfigMock);
        SamlAssertion samlAssertion = new SamlAssertion();

        sut.saveAssertion(ASSERTION_REF_1, samlAssertion);

        assertEquals(1, assertionMap.size());
        assertEquals(1, scheduledEvictionsMap.size());
        assertEquals(samlAssertion, assertionMap.get(ASSERTION_REF_1));

        CompletableFuture<Boolean> future = waitEvictionEnd(scheduledEvictionsMap);
        assertEquals(true, future.get());
        assertEquals(0, assertionMap.size());
        assertEquals(0, scheduledEvictionsMap.size());
    }

    @Test
    void getAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isAssertionStorageEnabled();

        sut = new SimpleAssertionStorage(new HashMap<>(), new HashMap<>(), storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);
    }

    @Test
    void savaAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isAssertionStorageEnabled();

        Map<String, SamlAssertion> assertionMap = new HashMap<>();
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();

        sut = new SimpleAssertionStorage(assertionMap, scheduledEvictionsMap, storageConfigMock);

        sut.saveAssertion(ASSERTION_REF_1, new SamlAssertion());

        assertEquals(0, assertionMap.size());
        assertEquals(0, scheduledEvictionsMap.size());
    }

    private CompletableFuture<Boolean> waitEvictionEnd(
            Map<String, ScheduledFuture<?>> scheduledEvictionsMap) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.submit(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        ScheduledFuture<?> scheduledFuture =
                                scheduledEvictionsMap.get(ASSERTION_REF_1);
                        scheduledFuture.get();
                        future.complete(true);
                    }
                });
        return future;
    }
}

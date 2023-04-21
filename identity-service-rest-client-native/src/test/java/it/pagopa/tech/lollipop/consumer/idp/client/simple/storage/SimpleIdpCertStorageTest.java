/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleIdpCertStorageTest {

    private static final long EVICTION_DELAY = 5000L;
    private static IdpCertStorageConfig storageConfigMock;
    private IdpCertStorage sut;
    private static final String IDPCERTDATA_1 = "1680691737https://posteid.poste.it";

    @BeforeEach
    void setUp() {
        storageConfigMock = mock(IdpCertStorageConfig.class);
        doReturn(EVICTION_DELAY).when(storageConfigMock).getStorageEvictionDelay();
        doReturn(TimeUnit.MILLISECONDS).when(storageConfigMock).getStorageEvictionDelayTimeUnit();
    }

    @Test
    void getExistingAssertionAndResetScheduleEvictionWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isIdpCertDataStorageEnabled();

        Map<String, IdpCertData> idpCertDataMap = new HashMap<>();
        IdpCertData idpCertData = new IdpCertData();
        idpCertDataMap.put(IDPCERTDATA_1, idpCertData);
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();
        ScheduledFuture<?> scheduledFutureMock = mock(ScheduledFuture.class);
        scheduledEvictionsMap.put(IDPCERTDATA_1, scheduledFutureMock);

        sut = new SimpleIdpCertStorage(idpCertDataMap, scheduledEvictionsMap, storageConfigMock);

        IdpCertData result = sut.getIdpCertData(IDPCERTDATA_1);

        assertNotNull(result);
        assertEquals(idpCertData, result);
        assertEquals(1, scheduledEvictionsMap.size());

        CompletableFuture<Boolean> future = waitEvictionEnd(scheduledEvictionsMap);
        assertEquals(true, future.get());
        assertEquals(0, idpCertDataMap.size());
        assertEquals(0, scheduledEvictionsMap.size());
    }

    @Test
    void getNotExistingAssertionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isIdpCertDataStorageEnabled();

        sut = new SimpleIdpCertStorage(new HashMap<>(), new HashMap<>(), storageConfigMock);

        IdpCertData result = sut.getIdpCertData(IDPCERTDATA_1);

        assertNull(result);
    }

    @Test
    void saveAssertionAndScheduleEvictionWithStorageEnabled()
            throws InterruptedException, ExecutionException {
        doReturn(true).when(storageConfigMock).isIdpCertDataStorageEnabled();

        Map<String, IdpCertData> idpCertDataMap = new HashMap<>();
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();

        sut = new SimpleIdpCertStorage(idpCertDataMap, scheduledEvictionsMap, storageConfigMock);
        IdpCertData idpCertData = new IdpCertData();

        sut.saveIdpCertData(IDPCERTDATA_1, idpCertData);

        assertEquals(1, idpCertDataMap.size());
        assertEquals(1, scheduledEvictionsMap.size());
        assertEquals(idpCertData, idpCertDataMap.get(IDPCERTDATA_1));

        CompletableFuture<Boolean> future = waitEvictionEnd(scheduledEvictionsMap);
        assertEquals(true, future.get());
        assertEquals(0, idpCertDataMap.size());
        assertEquals(0, scheduledEvictionsMap.size());
    }

    @Test
    void getAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isIdpCertDataStorageEnabled();

        sut = new SimpleIdpCertStorage(new HashMap<>(), new HashMap<>(), storageConfigMock);

        IdpCertData result = sut.getIdpCertData(IDPCERTDATA_1);

        assertNull(result);
    }

    @Test
    void savaAssertionWithStorageDisabled() {
        doReturn(false).when(storageConfigMock).isIdpCertDataStorageEnabled();

        Map<String, IdpCertData> idpCertDataMap = new HashMap<>();
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new HashMap<>();

        sut = new SimpleIdpCertStorage(idpCertDataMap, scheduledEvictionsMap, storageConfigMock);

        sut.saveIdpCertData(IDPCERTDATA_1, new IdpCertData());

        assertEquals(0, idpCertDataMap.size());
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
                                scheduledEvictionsMap.get(IDPCERTDATA_1);
                        scheduledFuture.get();
                        future.complete(true);
                    }
                });
        return future;
    }
}

package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleAssertionStorageTest {

    private static StorageConfig storageConfigMock;
    private AssertionStorage sut;
    private static final String ASSERTION_REF_1 = "assertionRef1";

    @BeforeEach
    void setUp() {
        storageConfigMock = mock(StorageConfig.class);
        doReturn(10000L).when(storageConfigMock).getStorageEvictionDelay();
    }

    @Test
    void getExistingAssertionAndResetScheduleEvictionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        Map<String, SamlAssertion> assertionMap = new HashMap<>();
        SamlAssertion samlAssertion = new SamlAssertion();
        assertionMap.put(ASSERTION_REF_1, samlAssertion);
        Map<String, Timer> timerMap = new HashMap<>();
        Timer timer = new Timer();
        timerMap.put(ASSERTION_REF_1, timer);

        sut = new SimpleAssertionStorage(assertionMap, timerMap, storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNotNull(result);
        assertEquals(samlAssertion, result);
        assertEquals(1, timerMap.size());
        assertNotEquals(timer, timerMap.get(ASSERTION_REF_1));

    }

    @Test
    void getNotExistingAssertionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        sut = new SimpleAssertionStorage(new HashMap<>(), new HashMap<>(), storageConfigMock);

        SamlAssertion result = sut.getAssertion(ASSERTION_REF_1);

        assertNull(result);
    }

    @Test
    void saveAssertionAndScheduleEvictionWithStorageEnabled() {
        doReturn(true).when(storageConfigMock).isAssertionStorageEnabled();

        Map<String, SamlAssertion> assertionMap = new HashMap<>();
        Map<String, Timer> timerMap = new HashMap<>();

        sut = new SimpleAssertionStorage(assertionMap, timerMap, storageConfigMock);
        SamlAssertion samlAssertion = new SamlAssertion();

        sut.saveAssertion(ASSERTION_REF_1, samlAssertion);

        assertEquals(1, assertionMap.size());
        assertEquals(1, timerMap.size());
        assertEquals(samlAssertion, assertionMap.get(ASSERTION_REF_1));
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
        Map<String, Timer> timerMap = new HashMap<>();

        sut = new SimpleAssertionStorage(assertionMap, timerMap, storageConfigMock);

        sut.saveAssertion(ASSERTION_REF_1, new SamlAssertion());

        assertEquals(0, assertionMap.size());
        assertEquals(0, timerMap.size());
    }
}
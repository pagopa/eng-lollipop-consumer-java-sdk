/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/** Implementation of the {@link AssertionStorage} interface as a simple in memory storage */
public class SimpleAssertionStorage implements AssertionStorage {

    private final Map<String, SamlAssertion> assertionMap;
    private final Map<String, Timer> timerMap;
    private final StorageConfig storageConfig;

    @Inject
    public SimpleAssertionStorage(
            Map<String, SamlAssertion> assertionMap,
            Map<String, Timer> timerMap,
            StorageConfig storageConfig) {
        this.assertionMap = assertionMap;
        this.timerMap = timerMap;
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
        TimerTask evictionTask =
                new TimerTask() {
                    public void run() {
                        assertionMap.remove(assertionRef);
                    }
                };
        Timer timer = new Timer();
        long delay = 1000L;
        timer.schedule(evictionTask, delay);
        timerMap.put(assertionRef, timer);
    }

    private void delayEviction(String assertionRef) {
        Timer timer = timerMap.get(assertionRef);
        timer.cancel();
        timerMap.remove(assertionRef);
        scheduleEviction(assertionRef);
    }
}

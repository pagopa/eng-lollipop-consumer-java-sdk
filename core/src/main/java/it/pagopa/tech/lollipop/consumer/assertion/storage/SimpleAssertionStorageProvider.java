package it.pagopa.tech.lollipop.consumer.assertion.storage;

import java.util.HashMap;

/**
 * Implementation of {@link AssertionStorageProvider} interface. It provides an instance of the
 */
public class SimpleAssertionStorageProvider implements AssertionStorageProvider {

    /**
     * {@inheritDoc}
     *
     * @return an instance of {@link SimpleAssertionStorage}
     */
    @Override
    public AssertionStorage provideStorage() {
        return new SimpleAssertionStorage(new HashMap<>(), new HashMap<>(), new StorageConfig(true, 60000L));
    }
}

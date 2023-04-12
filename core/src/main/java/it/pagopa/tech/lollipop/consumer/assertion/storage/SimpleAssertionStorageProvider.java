/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import java.util.HashMap;

/** Implementation of {@link AssertionStorageProvider} interface. It provides an instance of the */
public class SimpleAssertionStorageProvider implements AssertionStorageProvider {

    /**
     * {@inheritDoc}
     *
     * @param storageConfig the storage configuration
     * @return an instance of {@link SimpleAssertionStorage}
     */
    @Override
    public AssertionStorage provideStorage(StorageConfig storageConfig) {
        return new SimpleAssertionStorage(new HashMap<>(), new HashMap<>(), storageConfig);
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import lombok.Getter;

/** Configuration class for the assertion storage */
@Getter
public class StorageConfig {

    private final boolean assertionStorageEnabled;
    private final long storageEvictionDelay;

    public StorageConfig(boolean assertionStorageEnabled, long storageEvictionDelay) {
        this.assertionStorageEnabled = assertionStorageEnabled;
        this.storageEvictionDelay = storageEvictionDelay;
    }
}

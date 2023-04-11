/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/** Configuration class for the assertion storage */
@Data
public class StorageConfig {

    private boolean assertionStorageEnabled = true;
    private long storageEvictionDelay = 1L;
    private TimeUnit storageEvictionDelayTimeUnit = TimeUnit.MINUTES;
}

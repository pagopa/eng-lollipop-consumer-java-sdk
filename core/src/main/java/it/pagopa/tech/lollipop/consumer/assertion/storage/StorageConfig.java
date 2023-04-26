/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

import java.util.concurrent.TimeUnit;
import lombok.Data;

/** Configuration class for the assertion storage */
@Data
public class StorageConfig {

    private boolean assertionStorageEnabled = true;
    private long storageEvictionDelay = 1L;
    private TimeUnit storageEvictionDelayTimeUnit = TimeUnit.MINUTES;
    private long maxNumberOfElements = 100;
}

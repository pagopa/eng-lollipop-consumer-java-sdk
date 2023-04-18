/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.storage;

import java.util.concurrent.TimeUnit;
import lombok.Data;

/** Configuration class for the idpCertData storage */
@Data
public class IdpCertStorageConfig {

    private boolean idpCertDataStorageEnabled = true;
    private long storageEvictionDelay = 1L;
    private TimeUnit storageEvictionDelayTimeUnit = TimeUnit.MINUTES;
}

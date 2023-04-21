package it.pagopa.tech.lollipop.consumer.sample.config;

import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(
        prefix = "lollipop.assertion.storage.config"
)
@ConfigurationPropertiesScan
public class SpringAssertionStorageConfig extends StorageConfig {
}

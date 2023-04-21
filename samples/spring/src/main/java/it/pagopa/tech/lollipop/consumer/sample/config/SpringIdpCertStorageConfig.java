package it.pagopa.tech.lollipop.consumer.sample.config;

import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(
        prefix = "lollipop.idp.storage.config"
)
@ConfigurationPropertiesScan
public class SpringIdpCertStorageConfig extends IdpCertStorageConfig {}

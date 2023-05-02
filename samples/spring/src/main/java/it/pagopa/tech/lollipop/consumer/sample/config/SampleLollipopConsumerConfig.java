package it.pagopa.tech.lollipop.consumer.sample.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "sample.lollipop.consumer.config")
@ConfigurationPropertiesScan
@Data
public class SampleLollipopConsumerConfig {

    private String endpoint;
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring.config;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Spring instance of the {@link LollipopConsumerRequestConfig} */
@ConfigurationProperties(prefix = "lollipop.core.config")
@ConfigurationPropertiesScan
@NoArgsConstructor
public class SpringLollipopConsumerRequestConfig extends LollipopConsumerRequestConfig {}

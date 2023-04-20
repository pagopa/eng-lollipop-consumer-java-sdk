package it.pagopa.tech.lollipop.consumer.sample.config;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.SimpleAssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImpl;
import it.pagopa.tech.lollipop.consumer.spring.config.SpringLollipopConsumerRequestConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SpringLollipopConsumerRequestConfig.class})
public class SampleServicesConfig {

    @Bean
    public HttpMessageVerifierFactory httpMessageVerifierFactory(SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig) throws Exception {
        return new VismaHttpMessageVerifierFactory("UTF-8", springLollipopConsumerRequestConfig);
    }

    @Bean
    public IdpCertProviderFactory idpCertProviderFactory() {
        return new IdpCertProviderFactoryImpl(
                new IdpCertSimpleClientProvider(IdpCertSimpleClientConfig.builder().build()));
    }

    @Bean
    public AssertionServiceFactory assertionServiceFactory() {
        return new AssertionServiceFactoryImpl(
                new SimpleAssertionStorageProvider(),
                new AssertionSimpleClientProvider(AssertionSimpleClientConfig.builder().build()),
                storageConfig());
    }

    @Bean
    public StorageConfig storageConfig() {
        return new StorageConfig();
    }
}

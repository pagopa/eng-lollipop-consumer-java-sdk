/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet.config;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpVerifierConfiguration {

    @Bean
    public LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper(
            LollipopLoggerServiceFactory lollipopLoggerServiceFactory,
            HttpMessageVerifierFactory httpMessageVerifierFactory,
            IdpCertProviderFactory idpCertProviderFactory,
            AssertionServiceFactory assertionServiceFactory,
            LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService,
            LollipopConsumerRequestConfig lollipopConsumerRequestConfig) {
        return new LollipopConsumerFactoryHelper(
                lollipopLoggerServiceFactory,
                httpMessageVerifierFactory,
                idpCertProviderFactory,
                assertionServiceFactory,
                lollipopConsumerRequestValidationService,
                lollipopConsumerRequestConfig);
    }

    @Bean
    public LollipopConsumerRequestValidationService getLollipopConsumerRequestValidationService(
            LollipopConsumerRequestConfig lollipopConsumerRequestConfig) {
        return new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig);
    }

    @Bean
    public LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder(
            LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper) {
        return new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
    }
}

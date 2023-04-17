/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring.config;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import it.pagopa.tech.lollipop.consumer.spring.HttpVerifierHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpVerifierConfiguration {

    private SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig;

    @Bean
    public LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper(
            HttpMessageVerifierFactory httpMessageVerifierFactory,
            IdpCertProviderFactory idpCertProviderFactory,
            AssertionServiceFactory assertionServiceFactory) {
        return new LollipopConsumerFactoryHelper(
                httpMessageVerifierFactory,
                idpCertProviderFactory,
                assertionServiceFactory,
                getLollipopConsumerRequestValidationService());
    }

    @Bean
    public LollipopConsumerRequestValidationService getLollipopConsumerRequestValidationService() {
        return new LollipopConsumerRequestValidationServiceImpl(
                springLollipopConsumerRequestConfig);
    }

    @Bean
    public LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder(
            LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper) {
        return new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
    }

    @Bean
    public HttpVerifierHandlerInterceptor httpVerifierHandlerInterceptor(
            LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder) {
        return new HttpVerifierHandlerInterceptor(lollipopConsumerCommandBuilder);
    }
}

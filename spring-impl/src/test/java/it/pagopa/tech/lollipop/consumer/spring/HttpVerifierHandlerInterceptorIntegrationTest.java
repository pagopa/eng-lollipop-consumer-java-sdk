/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.SimpleAssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImplStub;
import it.pagopa.tech.lollipop.consumer.spring.config.HttpVerifierConfiguration;
import it.pagopa.tech.lollipop.consumer.spring.config.SpringLollipopConsumerRequestConfig;
import java.security.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
            HttpVerifierHandlerInterceptor.class,
            SpringLollipopConsumerRequestConfig.class,
            HttpVerifierConfiguration.class,
            HttpVerifierHandlerInterceptorIntegrationTest.DemoServicesConfig.class
        })
public class HttpVerifierHandlerInterceptorIntegrationTest {

    //    @Autowired
    //    private DemoController controller;
    //
    //    @LocalServerPort
    //    private int port;
    //
    //    @Controller
    //    public static class DemoController {
    //
    //        @PostMapping("/")
    //        public @ResponseBody
    //        String testMethod() {
    //            return "Test!";
    //        }
    //
    //    }

    @Autowired private HttpVerifierHandlerInterceptor interceptor;

    @Configuration
    public static class DemoServicesConfig {

        @Bean
        public SpringLollipopConsumerRequestConfig verifierConfiguration() {
            return new SpringLollipopConsumerRequestConfig();
        }

        @Bean
        public HttpMessageVerifierFactory httpMessageVerifierFactory() throws Exception {
            return new VismaHttpMessageVerifierFactory("UTF-8", verifierConfiguration());
        }

        @Bean
        public IdpCertProviderFactory idpCertProviderFactory() {
            return new IdpCertProviderFactoryImplStub();
        }

        @Bean
        public AssertionServiceFactory assertionServiceFactory() {
            return new AssertionServiceFactoryImpl(
                    new SimpleAssertionStorageProvider(),
                    new AssertionSimpleClientProvider(
                            AssertionSimpleClientConfig.builder().build()),
                    storageConfig());
        }

        @Bean
        public StorageConfig storageConfig() {
            return new StorageConfig();
        }
    }

    @Component
    public static class DemoWebConfigurer implements WebMvcConfigurer {

        @Autowired private HttpVerifierHandlerInterceptor interceptor;

        @Bean
        public CommonsRequestLoggingFilter loggingFilter() {
            CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
            filter.setIncludeQueryString(true);
            filter.setIncludeClientInfo(true);
            filter.setIncludeHeaders(true);
            filter.setIncludePayload(true);
            return filter;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(interceptor);
        }
    }

    @Test
    public void something() {}
}

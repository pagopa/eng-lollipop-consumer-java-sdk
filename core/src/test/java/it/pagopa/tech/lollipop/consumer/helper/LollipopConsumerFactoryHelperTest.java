/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LollipopConsumerFactoryHelperTest {

    IdpCertProviderFactory idpCertProviderFactory;
    HttpMessageVerifierFactory httpMessageVerifierFactory;
    LollipopLoggerServiceFactory lollipopLoggerServiceFactory;

    AssertionServiceFactory assertionServiceFactory;

    LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper;

    LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService;

    public LollipopConsumerFactoryHelperTest() {
        idpCertProviderFactory = Mockito.mock(IdpCertProviderFactory.class);
        lollipopLoggerServiceFactory = Mockito.mock(LollipopLoggerServiceFactory.class);
        httpMessageVerifierFactory = Mockito.mock(HttpMessageVerifierFactory.class);
        assertionServiceFactory = Mockito.mock(AssertionServiceFactory.class);
        lollipopConsumerRequestValidationService =
                Mockito.mock(LollipopConsumerRequestValidationService.class);
    }

    @BeforeEach
    public void init() {
        Mockito.reset(
                idpCertProviderFactory,
                lollipopLoggerServiceFactory,
                httpMessageVerifierFactory,
                assertionServiceFactory);
        lollipopConsumerFactoryHelper =
                new LollipopConsumerFactoryHelper(
                        lollipopLoggerServiceFactory,
                        httpMessageVerifierFactory,
                        idpCertProviderFactory,
                        assertionServiceFactory,
                        lollipopConsumerRequestValidationService,
                        LollipopConsumerRequestConfig.builder().build());
    }

    @Test
    public void testThatRetunsAssertionVerifierService() {
        assertThat(lollipopConsumerFactoryHelper.getAssertionVerifierService())
                .isInstanceOf(AssertionVerifierService.class);
    }

    @Test
    public void testThatRetunsHttpMessageVerifierService() {
        assertThat(lollipopConsumerFactoryHelper.getHttpMessageVerifierService())
                .isInstanceOf(HttpMessageVerifierService.class);
    }
}

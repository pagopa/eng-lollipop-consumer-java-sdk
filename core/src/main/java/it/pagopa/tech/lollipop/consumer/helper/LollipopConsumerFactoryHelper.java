/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.helper;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.logger.impl.LollipopLogbackLoggerService;
import it.pagopa.tech.lollipop.consumer.logger.impl.LollipopLogbackLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import it.pagopa.tech.lollipop.consumer.service.impl.AssertionVerifierServiceImpl;
import it.pagopa.tech.lollipop.consumer.service.impl.HttpMessageVerifierServiceImpl;
import javax.inject.Inject;

/** Helper class for retrieving instances */
public class LollipopConsumerFactoryHelper {

    private final HttpMessageVerifierFactory httpMessageVerifierFactory;
    private final IdpCertProviderFactory idpCertProviderFactory;
    private final LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService;
    private final AssertionServiceFactory assertionServiceFactory;

    private IdpCertProvider idpCertProvider;
    private AssertionService assertionService;

    private HttpMessageVerifierService httpMessageVerifierService;
    private AssertionVerifierService assertionVerifierService;
    private LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    @Inject
    public LollipopConsumerFactoryHelper(
            HttpMessageVerifierFactory httpMessageVerifierFactory,
            IdpCertProviderFactory idpCertProviderFactory,
            AssertionServiceFactory assertionServiceFactory,
            LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService) {
        this.httpMessageVerifierFactory = httpMessageVerifierFactory;
        this.idpCertProviderFactory = idpCertProviderFactory;
        this.assertionServiceFactory = assertionServiceFactory;
        this.lollipopConsumerRequestValidationService = lollipopConsumerRequestValidationService;
    }

    /**
     * Utility method for retrieving an instance of {@link HttpMessageVerifierService}
     *
     * @return an instance of {@link HttpMessageVerifierService}
     */
    public HttpMessageVerifierService getHttpMessageVerifierService() {
        return this.httpMessageVerifierService != null
                ? this.httpMessageVerifierService
                : new HttpMessageVerifierServiceImpl(
                        getHttpMessageVerifierFactory().create(),
                        getLollipopConsumerRequestConfig());
    }

    /**
     * Utility method for retrieving an instance of {@link AssertionVerifierService}
     *
     * @return an instance of {@link AssertionVerifierService}
     */
    public AssertionVerifierService getAssertionVerifierService() {

        return this.assertionVerifierService != null
                ? this.assertionVerifierService
                : new AssertionVerifierServiceImpl(
                        createIdpCertProvider(),
                        createAssertionService(),
                        getLollipopConsumerRequestConfig());
    }

    public HttpMessageVerifierFactory getHttpMessageVerifierFactory() {
        return httpMessageVerifierFactory;
    }

    public IdpCertProviderFactory getIdpCertProviderFactory() {
        return idpCertProviderFactory;
    }

    public AssertionServiceFactory getAssertionServiceFactory() {
        return assertionServiceFactory;
    }

    public LollipopConsumerRequestConfig getLollipopConsumerRequestConfig() {
        return lollipopConsumerRequestConfig != null
                ? lollipopConsumerRequestConfig
                : createDefaultConfig();
    }

    private synchronized LollipopConsumerRequestConfig createDefaultConfig() {

        if (this.lollipopConsumerRequestConfig == null) {
            this.lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder().build();
        }

        return this.lollipopConsumerRequestConfig;
    }

    private synchronized IdpCertProvider createIdpCertProvider() {

        if (this.idpCertProvider == null) {
            this.idpCertProvider = getIdpCertProviderFactory().create();
        }

        return this.idpCertProvider;
    }

    private synchronized AssertionService createAssertionService() {

        if (this.assertionService == null) {
            this.assertionService = getAssertionServiceFactory().create();
        }

        return this.assertionService;
    }

    public LollipopConsumerRequestValidationService getRequestValidationService() {
        return lollipopConsumerRequestValidationService;
    }

    public void setAssertionVerifierService(AssertionVerifierService assertionVerifierService) {
        this.assertionVerifierService = assertionVerifierService;
    }

    public LollipopLoggerService getLollipopLoggerService() {
        return new LollipopLogbackLoggerService();
    }
}

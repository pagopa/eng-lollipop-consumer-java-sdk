/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.helper;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.impl.AssertionVerifierServiceImpl;
import it.pagopa.tech.lollipop.consumer.service.impl.HttpMessageVerifierServiceImpl;
import javax.inject.Inject;

/** Helper class for retrieving instances */
public class LollipopConsumerFactoryHelper {

    private final HttpMessageVerifierFactory httpMessageVerifierFactory;
    private final IdpCertProviderFactory idpCertProviderFactory;
    private final AssertionServiceFactory assertionServiceFactory;

    @Inject
    public LollipopConsumerFactoryHelper(
            HttpMessageVerifierFactory httpMessageVerifierFactory,
            IdpCertProviderFactory idpCertProviderFactory,
            AssertionServiceFactory assertionServiceFactory) {
        this.httpMessageVerifierFactory = httpMessageVerifierFactory;
        this.idpCertProviderFactory = idpCertProviderFactory;
        this.assertionServiceFactory = assertionServiceFactory;
    }

    /**
     * Utility method for retrieving an instance of {@link HttpMessageVerifierService}
     *
     * @return an instance of {@link HttpMessageVerifierService}
     */
    public HttpMessageVerifierService getHttpMessageVerifierService() {
        return new HttpMessageVerifierServiceImpl(
                getHttpMessageVerifierFactory().create(),
                getLollipopConsumerRequestConfig()
        );
    }

    /**
     * Utility method for retrieving an instance of {@link AssertionVerifierService}
     *
     * @return an instance of {@link AssertionVerifierService}
     */
    public AssertionVerifierService getAssertionVerifierService() {
        return new AssertionVerifierServiceImpl(
                getIdpCertProviderFactory().create(),
                getAssertionServiceFactory().create(),
                getLollipopConsumerRequestConfig()
        );
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
        return LollipopConsumerRequestConfig.builder().build();
    }
}

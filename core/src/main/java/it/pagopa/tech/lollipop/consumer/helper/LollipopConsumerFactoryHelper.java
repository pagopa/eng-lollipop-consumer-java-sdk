/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.helper;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.impl.AssertionVerifierServiceImplStub;
import it.pagopa.tech.lollipop.consumer.service.impl.HttpMessageVerifierServiceImpl;
import javax.inject.Inject;

/** Helper class for retrieving instances */
public class LollipopConsumerFactoryHelper {

    private HttpMessageVerifierFactory httpMessageVerifierFactory;
    private IdpCertProviderFactory idpCertProviderFactory;
    private AssertionServiceFactory assertionServiceFactory;

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
                getHttpMessageVerifierFactory().create(), getLollipopConsumerRequestConfig());
    }

    /**
     * Utility method for retrieving an instance of {@link AssertionVerifierService}
     *
     * @return an instance of {@link AssertionVerifierService}
     */
    public AssertionVerifierService getAssertionVerifierService() {
        return new AssertionVerifierServiceImplStub(
                getIdpCertProviderFactory().create(), getAssertionServiceFactory().create());
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

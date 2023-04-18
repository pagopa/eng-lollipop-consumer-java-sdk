/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import javax.inject.Inject;

/**
 * Implementation of {@link IdpCertProviderFactory}, used to create instances of {@link
 * IdpCertProviderImpl}
 */
public class IdpCertProviderFactoryImpl implements IdpCertProviderFactory {

    private final IdpCertClientProvider idpCertClientProvider;

    @Inject
    public IdpCertProviderFactoryImpl(IdpCertClientProvider idpCertClientProvider) {
        this.idpCertClientProvider = idpCertClientProvider;
    }

    /**
     * Factory for creating an instance of {@link IdpCertProvider}
     *
     * @return an instance of {@link IdpCertProviderImpl}
     */
    @Override
    public IdpCertProvider create() {
        return new IdpCertProviderImpl(idpCertClientProvider.provideClient());
    }
}

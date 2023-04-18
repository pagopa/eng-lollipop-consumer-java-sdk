/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import javax.inject.Inject;

/** Provider class for retrieving an instance of {@link IdpCertSimpleClient} */
public class IdpCertSimpleClientProvider implements IdpCertClientProvider {

    private final IdpCertSimpleClientConfig idpClientConfig;

    @Inject
    public IdpCertSimpleClientProvider(IdpCertSimpleClientConfig config) {
        this.idpClientConfig = config;
    }
    /**
     * Provide an instance of {@link IdpCertSimpleClient}
     *
     * @return {@link IdpCertSimpleClient}
     */
    @Override
    public IdpCertClient provideClient() {
        SimpleIdpCertStorageProvider storageProvider = new SimpleIdpCertStorageProvider();
        return new IdpCertSimpleClient(
                new ApiClient(this.idpClientConfig),
                this.idpClientConfig,
                storageProvider.provideStorage(new IdpCertStorageConfig()));
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;
import javax.inject.Inject;

/** Provider class for retrieving an instance of {@link IdpCertSimpleClient} */
public class IdpCertSimpleClientProvider implements IdpCertClientProvider {

    private final IdpCertSimpleClientConfig idpClientConfig;
    private final IdpCertStorageProvider idpCertStorageProvider;
    private final IdpCertStorageConfig idpCertStorageConfig;

    @Inject
    public IdpCertSimpleClientProvider(
            IdpCertSimpleClientConfig config,
            IdpCertStorageProvider idpCertStorageProvider,
            IdpCertStorageConfig idpCertStorageConfig) {
        this.idpClientConfig = config;
        this.idpCertStorageProvider = idpCertStorageProvider;
        this.idpCertStorageConfig = idpCertStorageConfig;
    }
    /**
     * Provide an instance of {@link IdpCertSimpleClient}
     *
     * @return {@link IdpCertSimpleClient}
     */
    @Override
    public IdpCertClient provideClient() {
        return new IdpCertSimpleClient(
                new ApiClient(this.idpClientConfig),
                this.idpClientConfig,
                idpCertStorageProvider.provideStorage(idpCertStorageConfig));
    }
}

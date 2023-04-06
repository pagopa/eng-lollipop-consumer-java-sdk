/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;

public class IdpCertSimpleClientProvider implements IdpCertClientProvider {

    /**
     * Provide an instance of {@link IdpCertSimpleClient}
     *
     * @return {@link IdpCertSimpleClient}
     */
    @Override
    public IdpCertClient provideClient() {
        return new IdpCertSimpleClient(new ApiClient());
    }
}

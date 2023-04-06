/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import javax.inject.Inject;

public class IdpCertSimpleClient implements IdpCertClient {

    ApiClient apiClient;
    DefaultApi defaultApi;

    @Inject
    public IdpCertSimpleClient(ApiClient client) {
        this.apiClient = client;
        this.defaultApi = new DefaultApi(client);
    }

    /**
     * @param entityId
     * @param instant
     * @return
     */
    @Override
    public IdpCertData getCertData(String entityId, String instant) {
        return null;
    }
}

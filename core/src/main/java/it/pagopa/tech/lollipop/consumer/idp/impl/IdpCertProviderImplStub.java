/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;

public class IdpCertProviderImplStub implements IdpCertProvider {

    private IdpCertClient idpCertClient;
    private IdpCertStorage idpCertStorage;

    @Override
    public boolean getIdpCertData(String assertionInstant, String entityId) {
        return false;
    }
}

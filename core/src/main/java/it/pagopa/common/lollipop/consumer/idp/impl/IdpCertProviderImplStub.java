package it.pagopa.common.lollipop.consumer.idp.impl;

import it.pagopa.common.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.common.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.common.lollipop.consumer.idp.storage.IdpCertStorage;

public class IdpCertProviderImplStub implements IdpCertProvider {

    private IdpCertClient idpCertClient;
    private IdpCertStorage idpCertStorage;
    @Override
    public boolean getIdpCertData(String assertionInstant, String entityId) {
        return false;
    }
}

package it.pagopa.spid.common.lc.idp.impl;

import it.pagopa.spid.common.lc.idp.IdpCertProvider;
import it.pagopa.spid.common.lc.idp.client.IdpCertClient;
import it.pagopa.spid.common.lc.idp.storage.IdpCertStorage;

public class IdpCertProviderImplStub implements IdpCertProvider {

    private IdpCertClient idpCertClient;
    private IdpCertStorage idpCertStorage;
    @Override
    public boolean getIdpCertData(String assertionInstant, String entityId) {
        return false;
    }
}

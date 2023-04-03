package it.pagopa.spid.common.lc.idp.impl;

import it.pagopa.spid.common.lc.idp.IdpCertProvider;
import it.pagopa.spid.common.lc.idp.IdpCertProviderFactory;
import it.pagopa.spid.common.lc.idp.client.IdpCertClientProvider;
import it.pagopa.spid.common.lc.idp.storage.IdpCertStorageProvider;

public class IdpCertProviderFactoryImplStub implements IdpCertProviderFactory {

    private IdpCertClientProvider idpCertClientProvider;
    private IdpCertStorageProvider idpCertStorageProvider;
    @Override
    public IdpCertProvider create() {
        return null;
    }
}

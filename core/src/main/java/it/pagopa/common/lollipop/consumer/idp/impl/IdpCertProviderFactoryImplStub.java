package it.pagopa.common.lollipop.consumer.idp.impl;

import it.pagopa.common.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.common.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.common.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.common.lollipop.consumer.idp.storage.IdpCertStorageProvider;

public class IdpCertProviderFactoryImplStub implements IdpCertProviderFactory {

    private IdpCertClientProvider idpCertClientProvider;
    private IdpCertStorageProvider idpCertStorageProvider;
    @Override
    public IdpCertProvider create() {
        return null;
    }
}

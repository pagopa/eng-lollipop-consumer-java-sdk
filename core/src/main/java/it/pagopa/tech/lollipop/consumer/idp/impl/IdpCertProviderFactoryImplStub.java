package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageProvider;

public class IdpCertProviderFactoryImplStub implements IdpCertProviderFactory {

    private IdpCertClientProvider idpCertClientProvider;
    private IdpCertStorageProvider idpCertStorageProvider;
    @Override
    public IdpCertProvider create() {
        return null;
    }
}

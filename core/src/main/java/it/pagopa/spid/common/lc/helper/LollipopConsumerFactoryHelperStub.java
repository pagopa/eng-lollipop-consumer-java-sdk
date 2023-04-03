package it.pagopa.spid.common.lc.helper;

import it.pagopa.spid.common.lc.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.spid.common.lc.idp.IdpCertProviderFactory;
import it.pagopa.spid.common.lc.service.AssertionVerifierService;
import it.pagopa.spid.common.lc.service.HttpMessageVerifierService;

public class LollipopConsumerFactoryHelperStub {

    public HttpMessageVerifierService getHttpMessageVerifierService(){
        return null;
    }

    public AssertionVerifierService getAssertionVerifierService(){
        return null;
    }

    public HttpMessageVerifierFactory getHttpMessageVerifierFactory(){
        return null;
    }

    public IdpCertProviderFactory getIdpCertProviderFactory(){
        return null;
    }

}

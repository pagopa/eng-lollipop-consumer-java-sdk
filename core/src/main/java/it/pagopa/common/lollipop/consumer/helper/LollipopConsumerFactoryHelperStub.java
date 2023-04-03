package it.pagopa.common.lollipop.consumer.helper;

import it.pagopa.common.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.common.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.common.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.common.lollipop.consumer.service.HttpMessageVerifierService;

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

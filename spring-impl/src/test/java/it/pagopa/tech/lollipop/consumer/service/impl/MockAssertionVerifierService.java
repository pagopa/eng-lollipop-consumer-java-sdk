package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import org.w3c.dom.Document;

import java.util.List;

public class MockAssertionVerifierService extends AssertionVerifierServiceImpl {

    public MockAssertionVerifierService(IdpCertProvider idpCertProvider, AssertionService assertionService, LollipopConsumerRequestConfig lollipopRequestConfig) {
        super(idpCertProvider, assertionService, lollipopRequestConfig);
    }

    @Override
    protected boolean validateSignature(Document assertionDoc, List<IdpCertData> idpCertDataList) {
        return true;
    }
}

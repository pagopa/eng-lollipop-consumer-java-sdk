package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.HttpMessageVerifierConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class HttpMessageVerifierServiceImplTest {

    private HttpMessageVerifierConfig httpMessageVerifierConfig;
    private HttpMessageVerifier httpMessageVerifier;

    private HttpMessageVerifierServiceImpl httpMessageVerifierService;

    public HttpMessageVerifierServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        this.httpMessageVerifierConfig = HttpMessageVerifierConfig
                .builder()
                .contentDigestHeader("UTF_8")
                .build();
        this.httpMessageVerifier = Mockito.mock(HttpMessageVerifier.class);
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(httpMessageVerifier);
        this.httpMessageVerifierService = new HttpMessageVerifierServiceImpl(
                httpMessageVerifier,httpMessageVerifierConfig);
    }

    @Test
    public void validDigestIsProcessed() {

        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        //httpMessageVerifierService.verifyContentDigest(lollipopConsumerRequest);

    }

    private LollipopConsumerRequest getLollipopConsumerRequest() {
        return LollipopConsumerRequest.builder().build();
    }

}
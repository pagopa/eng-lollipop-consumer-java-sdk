package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.mockito.Mockito.when;

class HttpMessageVerifierServiceImplTest {

    final String VALID_DIGEST = "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=";
    final String VALID_PAYLOAD = "a valid message payload";
    final String INVALID_ENCODING = "UTF-326";

    private LollipopConsumerRequestConfig httpMessageVerifierConfig;
    private HttpMessageVerifier httpMessageVerifier;

    private HttpMessageVerifierServiceImpl httpMessageVerifierService;

    public HttpMessageVerifierServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        this.httpMessageVerifierConfig = LollipopConsumerRequestConfig
                .builder()
                .contentDigestHeader("Content-Digest")
                .contentEncodingHeader("Content-Encoding")
                .signatureHeader("Signature-Input")
                .build();
        this.httpMessageVerifier = Mockito.mock(HttpMessageVerifier.class);
    }

    @BeforeEach
    public void beforeEach() throws LollipopDigestException, UnsupportedEncodingException {
        Mockito.reset(httpMessageVerifier);
        when(httpMessageVerifier.verifyDigest(
                Mockito.eq(VALID_DIGEST),
                Mockito.eq(VALID_PAYLOAD),
                Mockito.eq("UTF-8"))).thenReturn(true);
        this.httpMessageVerifierService = new HttpMessageVerifierServiceImpl(
                httpMessageVerifier,httpMessageVerifierConfig);
    }

    @Test
    public void validDigestIsProcessed() throws LollipopDigestException, UnsupportedEncodingException {

        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        httpMessageVerifierService.verifyContentDigest(lollipopConsumerRequest);

    }

    private LollipopConsumerRequest getLollipopConsumerRequest() {

        HashMap<String,String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put("Content-Digest",VALID_DIGEST);
        lollipopHeaderParams.put("Content-Encoding", "UTF-8");
        lollipopHeaderParams.put("Signature-Input", "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\" " +
                "\"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=" +
                "\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg");

        return LollipopConsumerRequest
                .builder()
                .requestBody(VALID_PAYLOAD)
                .headerParams(lollipopHeaderParams)
                .build();
    }

}
/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class HttpMessageVerifierServiceImplTest {

    final String VALID_DIGEST = "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=";
    final String VALID_PAYLOAD = "a valid message payload";
    final String INVALID_PAYLOAD = "an invalid payload";
    final String INVALID_ENCODING = "UTF-326";
    final String VALID_ENCODING = "UTF-8";


    private LollipopConsumerRequestConfig httpMessageVerifierConfig;
    private HttpMessageVerifier httpMessageVerifier;

    private HttpMessageVerifierServiceImpl httpMessageVerifierService;

    public HttpMessageVerifierServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        this.httpMessageVerifierConfig =
                LollipopConsumerRequestConfig.builder()
                        .contentDigestHeader("Content-Digest")
                        .contentEncodingHeader("Content-Encoding")
                        .signatureHeader("Signature")
                        .signatureInputHeader("Signature-Input")
                        .build();
        this.httpMessageVerifier = Mockito.mock(HttpMessageVerifier.class);
    }

    @BeforeEach
    public void beforeEach() throws LollipopDigestException, UnsupportedEncodingException {
        Mockito.reset(httpMessageVerifier);
        when(httpMessageVerifier.verifyDigest(
                        Mockito.eq(VALID_DIGEST), Mockito.eq(VALID_PAYLOAD), Mockito.eq(VALID_ENCODING)))
                .thenReturn(true);
        when(httpMessageVerifier.verifyDigest(
                Mockito.eq(VALID_DIGEST), Mockito.eq(INVALID_PAYLOAD), Mockito.eq(VALID_ENCODING)))
                .thenReturn(false);
        this.httpMessageVerifierService =
                new HttpMessageVerifierServiceImpl(httpMessageVerifier, httpMessageVerifierConfig);
    }

    @Test
    public void validRequestIsProcessed() {
        assertThatNoException()
                .isThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(getLollipopConsumerRequest()));
    }

    @Test
    public void invalidRequestIsProcessedWithStrictDigestValidation() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        this.httpMessageVerifierConfig.setStrictDigestVerify(true);
        lollipopConsumerRequest.setRequestBody(INVALID_PAYLOAD);
        lollipopConsumerRequest.getHeaderParams().put(
                "Signature-Input",
                "sig1=(\"x-pagopa-lollipop-original-method\" "
                        + "\"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid="
                        + "\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg");
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Content-Digest does not match the request payload");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopDigestException.ErrorCode.INCORRECT_DIGEST);
                        });
    }

    @Test
    public void requestWithInvalidDigestThrowsException() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        lollipopConsumerRequest.setRequestBody(INVALID_PAYLOAD);
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Content-Digest does not match the request payload");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopDigestException.ErrorCode.INCORRECT_DIGEST);
                        });
    }

    @Test
    public void requestWithoutContentDigestToValidateThrowsException() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        lollipopConsumerRequest.getHeaderParams().remove("Content-Digest");
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Missing required Content-Digest for validation");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopDigestException.ErrorCode.MISSING_DIGEST);
                        });
    }

    @Test
    public void requestWithoutRequestBodyToValidateThrowsException() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        lollipopConsumerRequest.setRequestBody(null);
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Missing required payload for digest validation");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopDigestException.ErrorCode.MISSING_PAYLOAD);
                        });
    }

    @Test
    public void requestWithoutSignatureToValidateThrowsException() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        lollipopConsumerRequest.getHeaderParams().remove("Signature");
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopVerifierException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Missing Signature Header");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE);
                        });
    }

    @Test
    public void requestWithoutSignatureInputToValidateThrowsException() {
        LollipopConsumerRequest lollipopConsumerRequest = getLollipopConsumerRequest();
        lollipopConsumerRequest.getHeaderParams().remove("Signature-Input");
        // execute & verify
        assertThatThrownBy(() -> httpMessageVerifierService.verifyHttpMessage(lollipopConsumerRequest))
                .isInstanceOfSatisfying(
                        LollipopVerifierException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Missing Signature-Input Header");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(LollipopVerifierException.ErrorCode.MISSING_SIGNATURE_INPUT);
                        });
    }

    private LollipopConsumerRequest getLollipopConsumerRequest() {

        HashMap<String, String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put("Content-Digest", VALID_DIGEST);
        lollipopHeaderParams.put("Content-Encoding", VALID_ENCODING);
        lollipopHeaderParams.put(
                "Signature-Input",
                "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\" "
                    + "\"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid="
                    + "\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg");
        lollipopHeaderParams.put(
                "Signature", "sig1=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:");

        return LollipopConsumerRequest.builder()
                .requestBody(VALID_PAYLOAD)
                .headerParams(lollipopHeaderParams)
                .build();
    }

}

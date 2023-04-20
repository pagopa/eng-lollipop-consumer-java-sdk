/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.service.impl.MockAssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.spring.config.HttpVerifierConfiguration;
import it.pagopa.tech.lollipop.consumer.spring.config.SpringLollipopConsumerRequestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
            DemoApplication.class,
            SpringLollipopConsumerRequestConfig.class,
            HttpVerifierConfiguration.class,
        })
public class HttpVerifierHandlerInterceptorIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;
    @Autowired private LollipopConsumerFactoryHelper factoryHelper;

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(3000);
    }

    private static ClientAndServer mockServer;

    @Autowired private HttpVerifierHandlerInterceptor interceptor;

    @Test
    void testWithValidRequestReturnsSuccess() {
        AssertionSimpleClientTestUtils.createExpectationAssertionFound();
        factoryHelper.setAssertionVerifierService(
                new MockAssertionVerifierService(
                        factoryHelper.getIdpCertProviderFactory().create(),
                        factoryHelper.getAssertionServiceFactory().create(),
                        LollipopConsumerRequestConfig.builder().build()));

        String signatureInput =
                "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            "Content-Digest",
                                            "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-original-url",
                                            "https://api-app.io.pagopa.it/first-lollipop/sign");
                            request.getHeaders().add("x-pagopa-lollipop-original-method", "POST");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-public-key",
                                            "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                                + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                                + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                                + "iJQLTI1NiJ9");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-assertion-ref",
                                            "sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI");
                            request.getHeaders().add("x-pagopa-lollipop-assertion-type", "SAML");
                            request.getHeaders().add("x-pagopa-lollipop-auth-jwt", "aValidJWT");
                            request.getHeaders()
                                    .add("x-pagopa-lollipop-user-id", "AAAAAA89S20I111X");
                            request.getHeaders().add("Signature-Input", signatureInput);
                            request.getHeaders().add("Signature", signature);
                        });

        ResponseEntity<String> response =
                exec.postForEntity(
                        "http://localhost:" + port,
                        "{\"message\":\"a valid message payload\"}",
                        String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testWithinvalidPayloadRequestReturnsUnauthorized() {
        AssertionSimpleClientTestUtils.createExpectationAssertionFound();

        String signatureInput =
                "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            "Content-Digest",
                                            "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-original-url",
                                            "https://api-app.io.pagopa.it/first-lollipop/sign");
                            request.getHeaders().add("x-pagopa-lollipop-original-method", "POST");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-public-key",
                                            "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                                + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                                + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                                + "iJQLTI1NiJ9");
                            request.getHeaders()
                                    .add(
                                            "x-pagopa-lollipop-assertion-ref",
                                            "sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI");
                            request.getHeaders().add("x-pagopa-lollipop-assertion-type", "SAML");
                            request.getHeaders().add("x-pagopa-lollipop-auth-jwt", "aValidJWT");
                            request.getHeaders()
                                    .add("x-pagopa-lollipop-user-id", "AAAAAA89S20I111X");
                            request.getHeaders().add("Signature-Input", signatureInput);
                            request.getHeaders().add("Signature", signature);
                        });

        ResponseEntity<String> response =
                exec.postForEntity(
                        "http://localhost:" + port,
                        "{\"message\":\"an invalid message payload\"}",
                        String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(401, response.getStatusCodeValue());
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}

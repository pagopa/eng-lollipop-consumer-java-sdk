/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet;

import static it.pagopa.tech.lollipop.consumer.servlet.utils.SimpleClientsTestUtils.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.servlet.config.HttpVerifierConfiguration;
import it.pagopa.tech.lollipop.consumer.servlet.utils.SimpleClientsTestUtils;
import java.io.IOException;
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
            ServletDemoApplication.class,
            LollipopConsumerRequestConfig.class,
            HttpVerifierConfiguration.class
        })
public class ServletIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;
    @Autowired private LollipopConsumerRequestConfig lollipopConsumerRequestConfig;
    @Autowired private IdpCertSimpleClientConfig idpCertSimpleClientConfig;

    private static ClientAndServer mockServer;

    private static final String CONTENT_DIGEST =
            "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:";
    private static final String USER_ID = "GDNNWA12H81Y874F";
    private static final String SIGNATURE_INPUT =
            "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
    private static final String SIGNATURE =
            "sig123=:6scl8sMzJdyG/OrnJXHRM9ajmIjrJ/zrLUDqvfOxj2h51DUKztTua3vR1kSUj/c/VT1ioDlt1QIMARABhquewg==:";

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(3000, 3001);
    }

    @Test
    void testWithValidRequestReturnsSuccess() throws IOException {
        SimpleClientsTestUtils.createExpectationAssertionFound();
        SimpleClientsTestUtils.createExpectationIdpFound();
        lollipopConsumerRequestConfig.setAssertionNotBeforeDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        lollipopConsumerRequestConfig.setAssertionInstantDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        idpCertSimpleClientConfig.setBaseUri("http://localhost:3001");

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getContentDigestHeader(),
                                            CONTENT_DIGEST);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getOriginalURLHeader(),
                                            lollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalUrl());
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getOriginalMethodHeader(),
                                            lollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalMethod());
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getPublicKeyHeader(),
                                            VALID_PUBLIC_KEY);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getAssertionRefHeader(),
                                            ASSERTION_REF);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getAssertionTypeHeader(),
                                            "SAML");
                            request.getHeaders()
                                    .add(lollipopConsumerRequestConfig.getAuthJWTHeader(), JWT);
                            request.getHeaders()
                                    .add(lollipopConsumerRequestConfig.getUserIdHeader(), USER_ID);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getSignatureInputHeader(),
                                            SIGNATURE_INPUT);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getSignatureHeader(),
                                            SIGNATURE);
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
    void testWithInvalidPayloadRequestReturnsUnauthorized() throws IOException {
        SimpleClientsTestUtils.createExpectationAssertionFound();

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getContentDigestHeader(),
                                            CONTENT_DIGEST);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getOriginalURLHeader(),
                                            lollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalUrl());
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getOriginalMethodHeader(),
                                            lollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalMethod());
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getPublicKeyHeader(),
                                            VALID_PUBLIC_KEY);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getAssertionRefHeader(),
                                            ASSERTION_REF);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getAssertionTypeHeader(),
                                            "SAML");
                            request.getHeaders()
                                    .add(lollipopConsumerRequestConfig.getAuthJWTHeader(), JWT);
                            request.getHeaders()
                                    .add(lollipopConsumerRequestConfig.getUserIdHeader(), USER_ID);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getSignatureInputHeader(),
                                            SIGNATURE_INPUT);
                            request.getHeaders()
                                    .add(
                                            lollipopConsumerRequestConfig.getSignatureHeader(),
                                            SIGNATURE);
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

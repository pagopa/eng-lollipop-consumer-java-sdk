/* (C)2023-2025 */
package it.pagopa.tech.lollipop.consumer.spring;

import static it.pagopa.tech.lollipop.consumer.spring.SimpleClientsTestUtils.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.spring.config.HttpVerifierConfiguration;
import it.pagopa.tech.lollipop.consumer.spring.config.SpringLollipopConsumerRequestConfig;
import java.io.IOException;
import org.junit.jupiter.api.*;
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
    private TestRestTemplate restTemplate;
    @Autowired private SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig;
    @Autowired private HttpVerifierHandlerInterceptor interceptor;
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
    public static final String VALID_ORIGINAL_URL =
            "https://api-app.io.pagopa.it/first-lollipop/sign";

    @BeforeEach
    public void startServer() {
        restTemplate = new TestRestTemplate();
        mockServer = startClientAndServer(3000, 3001);
        idpCertSimpleClientConfig.setBaseUri("http://localhost:3001");
    }
    /*
       @Test
       void testWithValidRequestReturnsSuccess() throws IOException {
           SimpleClientsTestUtils.createExpectationAssertionFound();
           SimpleClientsTestUtils.createExpectationIdpFound();

           RestTemplate exec = restTemplate.getRestTemplate();
           exec.getClientHttpRequestInitializers()
                   .add(
                           request -> {
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getContentDigestHeader(),
                                               CONTENT_DIGEST);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getOriginalURLHeader(),
                                               VALID_ORIGINAL_URL);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getOriginalMethodHeader(),
                                               springLollipopConsumerRequestConfig
                                                       .getExpectedFirstLcOriginalMethod());
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getPublicKeyHeader(),
                                               VALID_PUBLIC_KEY);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getAssertionRefHeader(),
                                               ASSERTION_REF);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getAssertionTypeHeader(),
                                               "SAML");
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig.getAuthJWTHeader(),
                                               JWT);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig.getUserIdHeader(),
                                               USER_ID);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getSignatureInputHeader(),
                                               SIGNATURE_INPUT);
                               request.getHeaders()
                                       .add(
                                               springLollipopConsumerRequestConfig
                                                       .getSignatureHeader(),
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

    */

    @Test
    void testWithInvalidPayloadRequestReturnsUnauthorized() throws IOException {
        SimpleClientsTestUtils.createExpectationAssertionFound();

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getContentDigestHeader(),
                                            CONTENT_DIGEST);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getOriginalURLHeader(),
                                            VALID_ORIGINAL_URL);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getOriginalMethodHeader(),
                                            springLollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalMethod());
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getPublicKeyHeader(),
                                            VALID_PUBLIC_KEY);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getAssertionRefHeader(),
                                            ASSERTION_REF);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getAssertionTypeHeader(),
                                            "SAML");
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig.getAuthJWTHeader(),
                                            JWT);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig.getUserIdHeader(),
                                            USER_ID);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getSignatureInputHeader(),
                                            SIGNATURE_INPUT);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getSignatureHeader(),
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

    @Test
    void testWithInvalidURLRequestReturnsUnauthorized() throws IOException {
        SimpleClientsTestUtils.createExpectationAssertionFound();
        SimpleClientsTestUtils.createExpectationIdpFound();
        springLollipopConsumerRequestConfig.setAssertionExpireInDays(365);
        idpCertSimpleClientConfig.setBaseUri("http://localhost:3001");

        RestTemplate exec = restTemplate.getRestTemplate();
        exec.getClientHttpRequestInitializers()
                .add(
                        request -> {
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getContentDigestHeader(),
                                            CONTENT_DIGEST);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getOriginalURLHeader(),
                                            VALID_ORIGINAL_URL + "/another-path");
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getOriginalMethodHeader(),
                                            springLollipopConsumerRequestConfig
                                                    .getExpectedFirstLcOriginalMethod());
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getPublicKeyHeader(),
                                            VALID_PUBLIC_KEY);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getAssertionRefHeader(),
                                            ASSERTION_REF);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getAssertionTypeHeader(),
                                            "SAML");
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig.getAuthJWTHeader(),
                                            JWT);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig.getUserIdHeader(),
                                            USER_ID);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getSignatureInputHeader(),
                                            SIGNATURE_INPUT);
                            request.getHeaders()
                                    .add(
                                            springLollipopConsumerRequestConfig
                                                    .getSignatureHeader(),
                                            SIGNATURE);
                        });

        ResponseEntity<String> response =
                exec.postForEntity(
                        "http://localhost:" + port,
                        "{\"message\":\"a valid message payload\"}",
                        String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "^" + VALID_ORIGINAL_URL + "$",
                springLollipopConsumerRequestConfig.getExpectedFirstLcOriginalUrl());
        Assertions.assertEquals(401, response.getStatusCodeValue());
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }
}

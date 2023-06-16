/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import static it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientTestUtils.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import org.mockserver.verify.VerificationTimes;

import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.OidcAssertionNotSupported;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;

class AssertionSimpleClientTest {

    private static AssertionSimpleClient assertionSimpleClient;
    private static AssertionSimpleClientConfig assertionConfig;
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        assertionConfig = Mockito.spy(AssertionSimpleClientConfig.builder().build());
        assertionConfig.setBaseUri("http://localhost:2000");
        ApiClient client = new ApiClient(assertionConfig);
        assertionSimpleClient = new AssertionSimpleClient(client);
        mockServer = startClientAndServer(2000);
    }

    @Test
    void samlAssertionFound() throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        AssertionSimpleClientTestUtils.createExpectationAssertionFound();
        SamlAssertion response = assertionSimpleClient.getAssertion(JWT, ASSERTION_REF);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getAssertionRef());
        Assertions.assertNotNull(response.getAssertionData());
        Assertions.assertEquals(ASSERTION_REF, response.getAssertionRef());
        Assertions.assertEquals(XML_STRING, response.getAssertionData());

        mockServer.verify(
                request().withHeader("Ocp-Apim-Subscription-Key", "FakeSubscriptionKey"),
                VerificationTimes.exactly(1));
    }

    @Test
    void assertionNotFound() {
        AssertionSimpleClientTestUtils.createExpectationAssertionNotFound();
        // setup
        Assertions.assertThrows(
                LollipopAssertionNotFoundException.class,
                () -> assertionSimpleClient.getAssertion(JWT, WRONG_ASSERTION_REF));
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import static it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientTestUtils.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.OidcAssertionNotSupported;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.verify.VerificationTimes;

class AssertionSimpleClientProviderTest {

    private static AssertionClient assertionClient;
    private static AssertionSimpleClientConfig assertionConfig;
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        assertionConfig = Mockito.spy(AssertionSimpleClientConfig.builder().build());
        assertionConfig.setBaseUri("http://localhost:2000");
        AssertionSimpleClientProvider provider = new AssertionSimpleClientProvider(assertionConfig);
        assertionClient = provider.provideClient();
        mockServer = startClientAndServer(2000);
    }

    @Test
    void requestContainsAuthHeader()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        AssertionSimpleClientTestUtils.createExpectationAssertionFound();

        assertionClient.getAssertion(JWT, ASSERTION_REF);

        mockServer.verify(
                request().withHeader("Ocp-Apim-Subscription-Key", "FakeSubscriptionKey"),
                VerificationTimes.exactly(1));
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}

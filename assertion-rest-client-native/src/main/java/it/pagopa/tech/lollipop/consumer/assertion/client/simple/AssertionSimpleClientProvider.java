package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;

/**
 * Provider class for retrieving an instance of {@link AssertionSimpleClient}
 */
public class AssertionSimpleClientProvider implements AssertionClientProvider {

    /**
     * Provide an instance of {@link AssertionSimpleClient}
     * @return {@link AssertionSimpleClient}
     */
    @Override
    public AssertionClient provideClient() {
        return new AssertionSimpleClient(new ApiClient());
    }
}

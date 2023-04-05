package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;

public class AssertionSimpleClientProvider implements AssertionClientProvider {
    @Override
    public AssertionClient provideClient() {
        AssertionClient client = new AssertionSimpleClient(new ApiClient());

        return client;
    }
}

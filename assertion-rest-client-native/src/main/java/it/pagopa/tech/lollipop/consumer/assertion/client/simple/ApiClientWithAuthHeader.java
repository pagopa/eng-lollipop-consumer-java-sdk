/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;
import java.lang.reflect.Field;
import java.net.http.HttpRequest;
import java.util.function.Consumer;

/** An ApiClient adapter for adding missing auth header to request. */
public class ApiClientWithAuthHeader extends ApiClient {

    public ApiClientWithAuthHeader(AssertionSimpleClientConfig config) {
        super(config);

        Consumer<HttpRequest.Builder> interceptor =
                (t -> t.header("Ocp-Apim-Subscription-Key", config.getSubscriptionKey()));

        Field interceptorField;
        try {
            // Use refection to access `interceptor` private field.
            interceptorField = ApiClient.class.getDeclaredField("interceptor");
            interceptorField.setAccessible(true);
            interceptorField.set(this, interceptor);
            interceptorField.setAccessible(false);
        } catch (NoSuchFieldException
                | SecurityException
                | IllegalArgumentException
                | IllegalAccessException e) {
            // Do not send header
        }
    }
}

/* (C)2024 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssertionSimpleClientConfig {

    @Builder.Default private String baseUri = "http://localhost:3000";

    @Builder.Default private String assertionRequestEndpoint = "/assertions";

    @Builder.Default private String subscriptionKey = "FakeSubscriptionKey";

    private Duration readTimeout;

    private Duration connectionTimeout;
}

/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

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

    @Builder.Default private String SubscriptionKey = "FakeSubscriptionKey";
}

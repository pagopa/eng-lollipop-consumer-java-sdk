/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssertionSimpleClientConfig {

    @Builder.Default private String baseUri = "http://localhost:3000";

    @Builder.Default private String assertionRequestEndpoint = "/assertions";
}

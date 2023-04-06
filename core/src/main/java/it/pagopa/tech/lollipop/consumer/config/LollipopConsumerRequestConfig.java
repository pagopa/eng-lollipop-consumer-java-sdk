/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LollipopConsumerRequestConfig {

    @Builder.Default private String signatureHeader = "signature";
    @Builder.Default private String signatureInputHeader = "signature-input";
    @Builder.Default private String contentEncodingHeader = "content-encoding";
    @Builder.Default private String contentDigestHeader = "content-digest";
    @Builder.Default private boolean strictDigestVerify = false;
}

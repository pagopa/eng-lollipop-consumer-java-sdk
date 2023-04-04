/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LollipopConsumerRequestConfig {

    private String signatureHeader = "signature";
    private String signatureInputHeader = "signature-input";
    private String contentEncodingHeader = "content-encoding";
    private String contentDigestHeader = "content-digest";
}

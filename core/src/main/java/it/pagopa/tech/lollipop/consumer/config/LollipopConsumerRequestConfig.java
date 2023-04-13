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

    @Builder.Default private String originalMethodHeader = "x-pagopa-lollipop-original-method";
    @Builder.Default private String originalURLHeader = "x-pagopa-lollipop-original-url";

    @Builder.Default private String expectedFirstLcOriginalMethod = "POST";
    @Builder.Default private String expectedFirstLcOriginalUrl = " https://api-app.io.pagopa.it/first-lollipop/sign";

}

package it.pagopa.tech.lollipop.consumer.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HttpMessageVerifierConfig {

    private String contentDigestHeader;

}

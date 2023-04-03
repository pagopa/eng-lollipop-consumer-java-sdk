package it.pagopa.common.lollipop.consumer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LollipopConsumerRequest {
    private String requestBody;
    private Map<String, String> requestParams;
    private Map<String, String> headerParams;
}

package it.pagopa.tech.lollipop.consumer.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Builder
public class LollipopConsumerRequest {
    private String requestBody;
    private Map<String, String> requestParams;
    private Map<String, String> headerParams;
}

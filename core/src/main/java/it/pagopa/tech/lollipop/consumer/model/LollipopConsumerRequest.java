/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LollipopConsumerRequest {
    private String requestBody;
    private Map<String, String> requestParams;
    private Map<String, String> headerParams;
}

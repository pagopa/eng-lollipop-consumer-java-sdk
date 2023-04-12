/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;

/** EntityDescriptor */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class EntityDescriptor {
    @JsonProperty("entityID")
    private String entityID;

    private String signature;

    @SuppressWarnings("unchecked")
    @JsonProperty("Signature")
    private void unpackNestedSignature(Map<String, Object> signature) {
        Map<String, Object> keyInfo = (Map<String, Object>) signature.get("KeyInfo");
        Map<String, Object> x509Data = (Map<String, Object>) keyInfo.get("X509Data");
        this.signature = (String) x509Data.get("X509Certificate");
    }
}

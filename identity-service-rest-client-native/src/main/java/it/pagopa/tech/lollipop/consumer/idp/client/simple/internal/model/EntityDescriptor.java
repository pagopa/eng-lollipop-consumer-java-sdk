/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/** EntityDescriptor */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class EntityDescriptor {
    private static final String KEY_DESCRIPTOR = "KeyDescriptor";
    private static final String KEY_INFO = "KeyInfo";
    private static final String X_509_DATA = "X509Data";
    private static final String X_509_CERTIFICATE = "X509Certificate";

    @JsonProperty("entityID")
    private String entityID;

    private String signature;

    /**
     * Methods that obtains the signature used for verify the user's assertion from a series of
     * nested objects contained in the idp certification xml
     *
     * @param signature HashMap pulled from the idp certification xml
     */
    @SuppressWarnings("unchecked")
    @JsonProperty("IDPSSODescriptor")
    private void unpackNestedSignature(Map<String, Object> signature) {
        Map<String, Object> keyDescriptor = new HashMap<>();
        if (signature.get(KEY_DESCRIPTOR) instanceof List) {
            List<Map<String, Object>> listDescriptors =
                    (List<Map<String, Object>>) signature.get(KEY_DESCRIPTOR);
            Optional<Map<String, Object>> optionalFirst =
                    listDescriptors.stream()
                            .filter(el -> el.get("use").equals("signing"))
                            .findFirst();

            if (optionalFirst.isPresent()) {
                keyDescriptor = optionalFirst.get();
            }
        } else {
            keyDescriptor = (Map<String, Object>) signature.get(KEY_DESCRIPTOR);
        }

        Map<String, Object> keyInfo = (Map<String, Object>) keyDescriptor.get(KEY_INFO);
        Map<String, Object> x509Data = (Map<String, Object>) keyInfo.get(X_509_DATA);
        this.signature = (String) x509Data.get(X_509_CERTIFICATE);
    }
}

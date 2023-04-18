/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.util.stream.Collectors;
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

    private List<String> signatureList;

    /**
     * Methods that obtains the signature used for verify the user's assertion from a series of
     * nested objects contained in the idp certification xml
     *
     * @param signature HashMap pulled from the idp certification xml
     */
    @SuppressWarnings("unchecked")
    @JsonProperty("IDPSSODescriptor")
    private void unpackNestedSignature(Map<String, Object> signature) {
        List<Map<String, Object>> keyDescriptorsList = new ArrayList<>();
        if (signature.get(KEY_DESCRIPTOR) instanceof List) {

            keyDescriptorsList =
                    ((List<Map<String, Object>>) signature.get(KEY_DESCRIPTOR))
                            .stream()
                                    .filter(el -> el.get("use").equals("signing"))
                                    .collect(Collectors.toList());
        } else {
            Map<String, Object> keyDescriptorFound =
                    (Map<String, Object>) signature.get(KEY_DESCRIPTOR);

            if (keyDescriptorFound.get("use").equals("signing")) {
                keyDescriptorsList.add(keyDescriptorFound);
            }
        }

        List<Map<String, Object>> keyInfosList = new ArrayList<>();
        for (Map<String, Object> keyDescriptor : keyDescriptorsList) {
            if (keyDescriptor.get(KEY_INFO) instanceof List) {
                keyInfosList = (List<Map<String, Object>>) keyDescriptor.get(KEY_INFO);
            } else {
                Map<String, Object> keyInfo = (Map<String, Object>) keyDescriptor.get(KEY_INFO);

                keyInfosList.add(keyInfo);
            }
        }

        List<Map<String, Object>> listX509Data = new ArrayList<>();
        for (Map<String, Object> keyInfo : keyInfosList) {
            if (keyInfo.get(X_509_DATA) instanceof List) {
                listX509Data = (List<Map<String, Object>>) keyInfo.get(X_509_DATA);
            } else {
                listX509Data.add((Map<String, Object>) keyInfo.get(X_509_DATA));
            }
        }

        List<String> signatureList = new ArrayList<>();
        for (Map<String, Object> x509Data : listX509Data) {
            if (x509Data.get(X_509_CERTIFICATE) instanceof List) {
                signatureList = (List<String>) x509Data.get(X_509_CERTIFICATE);
            } else {
                signatureList.add((String) x509Data.get(X_509_CERTIFICATE));
            }
        }

        this.signatureList = signatureList;
    }
}

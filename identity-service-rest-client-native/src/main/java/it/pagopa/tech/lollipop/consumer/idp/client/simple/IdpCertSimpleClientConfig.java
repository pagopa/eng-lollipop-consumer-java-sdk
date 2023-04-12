/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdpCertSimpleClientConfig {

    @Builder.Default
    private List<String> cieEntityId =
            List.of("https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO");

    @Builder.Default private String baseUri = "https://api.is.eng.pagopa.it";

    @Builder.Default private String idpKeysCieEndpoint = "/idp-keys/cie";

    @Builder.Default private String idpKeysSpidEndpoint = "/idp-keys/spid";
}

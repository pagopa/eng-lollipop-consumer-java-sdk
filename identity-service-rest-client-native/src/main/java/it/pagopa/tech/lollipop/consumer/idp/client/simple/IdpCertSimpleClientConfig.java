package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdpCertSimpleClientConfig {

    @Builder.Default
    private List<String> cieEntityId = List.of("https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO");

}

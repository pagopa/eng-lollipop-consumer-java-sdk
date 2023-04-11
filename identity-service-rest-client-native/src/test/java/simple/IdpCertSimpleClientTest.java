package simple;


import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class IdpCertSimpleClientTest {

    private static IdpCertSimpleClient idpCertSimpleClient;

    private static final String instant = "1679072970";
    private static final String SPID_ENTITY_ID = "https://posteid.poste.it";
    private static final String CIE_ENTITY_ID = "https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO";

    @BeforeAll
    public static void startServer() {
        ApiClient client = new ApiClient();
        idpCertSimpleClient = new IdpCertSimpleClient(client);
    }
    @Test
    void getSPIDCertData() {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(SPID_ENTITY_ID, instant);

        Assertions.assertNotNull(response);
    }

    @Test
    void getCIECertData() {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(CIE_ENTITY_ID, instant);

        Assertions.assertNotNull(response);
    }
}
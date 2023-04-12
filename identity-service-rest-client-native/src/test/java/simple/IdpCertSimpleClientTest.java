package simple;


import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.CertDataTagListNotFoundException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

class IdpCertSimpleClientTest {

    private static IdpCertSimpleClient idpCertSimpleClient;
    private static IdpCertSimpleClientConfig entityConfig;

    private static final String INSTANT = String.valueOf(Instant.now().getEpochSecond());
    private static final String SPID_ENTITY_ID = "https://posteid.poste.it";
    private static final String CIE_ENTITY_ID = "https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO";

    private static final String WRONG_ENTITY_ID = "https://wrongEntityID.it";
    private static final String WRONG_INSTANT = "xxxxxx";

    @BeforeAll
    public static void startServer() {
        ApiClient client = new ApiClient();
        entityConfig = Mockito.spy(IdpCertSimpleClientConfig.builder().build());
        idpCertSimpleClient = new IdpCertSimpleClient(client, entityConfig);
    }

    @Test
    void certSPIDDataFound() throws CertDataTagListNotFoundException, CertDataNotFoundException {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(SPID_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void certCIEDataFound() throws CertDataTagListNotFoundException, CertDataNotFoundException {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(CIE_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void getCertDataWrongEntityID() throws CertDataTagListNotFoundException, CertDataNotFoundException {
        Assertions.assertThrows(CertDataNotFoundException.class, () -> idpCertSimpleClient.getCertData(WRONG_ENTITY_ID, INSTANT));
    }

    @Test
    void getSPIDCertDataWrongInstant() throws CertDataTagListNotFoundException, CertDataNotFoundException {
        Assertions.assertThrows(CertDataTagListNotFoundException.class, () -> idpCertSimpleClient.getCertData(SPID_ENTITY_ID, WRONG_INSTANT));
    }

    @Test
    void getCIECertDataWrongInstant() throws CertDataTagListNotFoundException, CertDataNotFoundException {
        Assertions.assertThrows(CertDataTagListNotFoundException.class, () -> idpCertSimpleClient.getCertData(CIE_ENTITY_ID, WRONG_INSTANT));
    }

}
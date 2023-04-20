/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IdpCertSimpleClientTest {

    private static IdpCertSimpleClient idpCertSimpleClient;
    private static IdpCertSimpleClientConfig entityConfig;

    private static final String INSTANT = String.valueOf(Instant.now().getEpochSecond());
    private static final String SPID_ENTITY_ID = "https://posteid.poste.it";
    private static final String SPID_ENTITY_ID_MULTIPLE_SIGNATURE = "https://loginspid.aruba.it";
    private static final String CIE_ENTITY_ID =
            "https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO";

    private static final String WRONG_ENTITY_ID = "https://wrongEntityID.it";
    private static final String WRONG_INSTANT = "xxxxxx";

    @BeforeAll
    public static void startServer() {
        entityConfig = Mockito.spy(IdpCertSimpleClientConfig.builder().build());
        ApiClient client = new ApiClient(entityConfig);
        SimpleIdpCertStorageProvider storageProvider = new SimpleIdpCertStorageProvider();
        idpCertSimpleClient =
                new IdpCertSimpleClient(
                        client,
                        entityConfig,
                        storageProvider.provideStorage(new IdpCertStorageConfig()));
    }

    @Test
    void certSPIDDataFound() throws CertDataNotFoundException {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(SPID_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void certSPIDDataFoundMultipleSignature() throws CertDataNotFoundException {
        List<IdpCertData> response =
                idpCertSimpleClient.getCertData(SPID_ENTITY_ID_MULTIPLE_SIGNATURE, INSTANT);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.get(0).getCertData().size() > 1);
    }

    @Test
    void certCIEDataFound() throws CertDataNotFoundException {
        List<IdpCertData> response = idpCertSimpleClient.getCertData(CIE_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void getCertDataWrongEntityID() {
        Assertions.assertThrows(
                CertDataNotFoundException.class,
                () -> idpCertSimpleClient.getCertData(WRONG_ENTITY_ID, INSTANT));
    }

    @Test
    void getSPIDCertDataWrongInstant() {
        Assertions.assertThrows(
                CertDataNotFoundException.class,
                () -> idpCertSimpleClient.getCertData(SPID_ENTITY_ID, WRONG_INSTANT));
    }

    @Test
    void getCIECertDataWrongInstant() {
        Assertions.assertThrows(
                CertDataNotFoundException.class,
                () -> idpCertSimpleClient.getCertData(CIE_ENTITY_ID, WRONG_INSTANT));
    }

    @Test
    void entityIdNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> idpCertSimpleClient.getCertData(null, WRONG_INSTANT));
    }

    @Test
    void instantNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> idpCertSimpleClient.getCertData(CIE_ENTITY_ID, null));
    }
}

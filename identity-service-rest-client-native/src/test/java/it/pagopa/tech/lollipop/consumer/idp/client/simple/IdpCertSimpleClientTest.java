/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

class IdpCertSimpleClientTest {

    private static IdpCertSimpleClient idpCertSimpleClient;

    private static ClientAndServer mockServer;

    private static final String INSTANT = String.valueOf(Instant.now().getEpochSecond());
    private static final String TAG1 = String.valueOf(Instant.now().getEpochSecond() - 1);
    private static final String TAG2 = String.valueOf(Instant.now().getEpochSecond() + 1);
    private static final String SPID_ENTITY_ID = "https://posteid.poste.it";
    private static final String SPID_ENTITY_ID_MULTIPLE_SIGNATURE = "https://loginspid.aruba.it";
    private static final String CIE_ENTITY_ID =
            "https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO";

    private static final String WRONG_ENTITY_ID = "https://wrongEntityID.it";
    private static final String WRONG_INSTANT = "xxxxxx";

    @BeforeAll
    static void startServer() {
        mockServer = startClientAndServer(3001);
        IdpCertSimpleClientConfig entityConfig = spy(IdpCertSimpleClientConfig.builder().build());
//        IdpCertSimpleClientConfig entityConfig = IdpCertSimpleClientConfig.builder().baseUri("http://localhost:3001").build();
        ApiClient client = new ApiClient(entityConfig);
        SimpleIdpCertStorageProvider storageProvider = new SimpleIdpCertStorageProvider();
        idpCertSimpleClient =
                new IdpCertSimpleClient(
                        client,
                        entityConfig,
                        storageProvider.provideStorage(new IdpCertStorageConfig()));
        doReturn("http://localhost:3001").when(entityConfig).getBaseUri();
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    void certSPIDDataFound() throws CertDataNotFoundException {
        createExpectationIdpSpidFound();
        List<IdpCertData> response = idpCertSimpleClient.getCertData(SPID_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void certSPIDDataWithCustomEntityIdAndInstant() throws CertDataNotFoundException {
        var assertionInstant = "2024-07-24T23:18:38.435Z"; //to modify with instant from xml file of assertion
        var instantForLollipop = parseInstantToUnixTimestamp(assertionInstant);
        var entityId = "https://posteid.poste.it";
        createExpectationIdpSpidFound();
        List<IdpCertData> response = idpCertSimpleClient.getCertData(entityId, instantForLollipop);

        Assertions.assertNotNull(response);
    }

    @Test
    void certSPIDDataFoundMultipleSignature() throws CertDataNotFoundException {
        createExpectationIdpSpidFound();
        List<IdpCertData> response =
                idpCertSimpleClient.getCertData(SPID_ENTITY_ID_MULTIPLE_SIGNATURE, INSTANT);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.get(0).getCertData().size() > 1);
    }

    @Test
    void certCIEDataFound() throws CertDataNotFoundException {
        createExpectationIdpCieFound();
        List<IdpCertData> response = idpCertSimpleClient.getCertData(CIE_ENTITY_ID, INSTANT);

        Assertions.assertNotNull(response);
    }

    @Test
    void getCertDataWrongEntityID() {
        createExpectationIdpSpidFound();
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

    public static void createExpectationIdpSpidFound() {
        new MockServerClient("localhost", 3001)
                .when(request().withMethod("GET").withPath("/idp-keys/spid"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("[\"" + TAG1 + "\",\"" + TAG2 + "\"]"));
        new MockServerClient("localhost", 3001)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/idp-keys/spid/{tag}")
                                .withPathParameter("tag", TAG1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(retrieveDataFromFile("idp_spid_data_tag1.xml")));
        new MockServerClient("localhost", 3001)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/idp-keys/spid/{tag}")
                                .withPathParameter("tag", TAG2))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(retrieveDataFromFile("idp_spid_data_tag2.xml")));
    }

    public static void createExpectationIdpCieFound() {
        new MockServerClient("localhost", 3001)
                .when(request().withMethod("GET").withPath("/idp-keys/cie"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("[\"" + TAG1 + "\",\"" + TAG2 + "\"]"));
        new MockServerClient("localhost", 3001)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/idp-keys/cie/{tag}")
                                .withPathParameter("tag", TAG1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(retrieveDataFromFile("idp_cie_data_tag1.xml")));
        new MockServerClient("localhost", 3001)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/idp-keys/cie/{tag}")
                                .withPathParameter("tag", TAG2))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(retrieveDataFromFile("idp_cie_data_tag1.xml")));
    }

    @SneakyThrows
    private static String retrieveDataFromFile(String fileName) {
        FileInputStream fis = new FileInputStream("src/test/resources/" + fileName);
        return IOUtils.toString(fis, StandardCharsets.UTF_8);
    }

    private String parseInstantToUnixTimestamp(String instant) {
        try {
            instant =
                    Long.toString(
                            ISODateTimeFormat.dateTimeParser().parseDateTime(instant).getMillis()
                                    / 1000);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            System.err.println(e);
        }
        return instant;
    }
}

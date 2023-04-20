/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IdpCertProviderImplTest {

    private IdpCertClient idpCertClientMock;
    private IdpCertProvider sut;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        idpCertClientMock = Mockito.mock(IdpCertClient.class);
        sut = new IdpCertProviderImpl(idpCertClientMock);
    }

    @Test
    void getIdpCertDataSuccess() {
        String randomString1 = generateRandomString();
        String randomString2 = generateRandomString();
        Assertions.assertDoesNotThrow(() -> sut.getIdpCertData(randomString1, randomString2));
    }

    @Test
    void getIdpCertDataErrorInvalidInstantNull() {
        String randomString = generateRandomString();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getIdpCertData(null, randomString));
    }

    @Test
    void getIdpCertDataErrorInvalidInstantEmpty() {
        String randomString = generateRandomString();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getIdpCertData("", randomString));
    }

    @Test
    void getIdpCertDataErrorInvalidEntityIdNull() {
        String randomString = generateRandomString();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getIdpCertData(randomString, null));
    }

    @Test
    void getIdpCertDataErrorInvalidEntityIdEmpty() {
        String randomString = generateRandomString();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getIdpCertData(randomString, ""));
    }

    private String generateRandomString() {
        byte[] array = new byte[7];
        random.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}

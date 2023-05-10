/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorage;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.OidcAssertionNotSupported;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssertionServiceImplTest {

    private static AssertionStorage assertionStorageMock;
    private static AssertionClient assertionClientMock;
    private static AssertionService sut;

    private static final String JWT = "jwt";
    private static final String ASSERTION_REF = "assertionRef";

    @BeforeEach
    void setUp() {
        assertionClientMock = mock(AssertionClient.class);
        assertionStorageMock = mock(AssertionStorage.class);
        sut = new AssertionServiceImpl(assertionStorageMock, assertionClientMock);
    }

    @Test
    void getAssertionFromStorageWithSuccess()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        SamlAssertion assertion = new SamlAssertion();
        doReturn(assertion).when(assertionStorageMock).getAssertion(ASSERTION_REF);

        sut.getAssertion(JWT, ASSERTION_REF);

        verify(assertionStorageMock).getAssertion(ASSERTION_REF);
        verify(assertionClientMock, never()).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(ASSERTION_REF, assertion);
    }

    @Test
    void getAssertionFromClientWithSuccess()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        SamlAssertion assertion = new SamlAssertion();
        doReturn(null).when(assertionStorageMock).getAssertion(ASSERTION_REF);
        doReturn(assertion).when(assertionClientMock).getAssertion(JWT, ASSERTION_REF);

        sut.getAssertion(JWT, ASSERTION_REF);

        verify(assertionStorageMock).getAssertion(ASSERTION_REF);
        verify(assertionClientMock).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock).saveAssertion(ASSERTION_REF, assertion);
    }

    @Test
    void getUnsupportedAssertionFromClient()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        doReturn(null).when(assertionStorageMock).getAssertion(ASSERTION_REF);
        doReturn(null).when(assertionClientMock).getAssertion(JWT, ASSERTION_REF);

        sut.getAssertion(JWT, ASSERTION_REF);

        verify(assertionStorageMock).getAssertion(ASSERTION_REF);
        verify(assertionClientMock).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any());
    }

    @Test
    void getAssertionFromClientWithLollipopAssertionNotFoundException()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        doReturn(null).when(assertionStorageMock).getAssertion(ASSERTION_REF);
        doThrow(LollipopAssertionNotFoundException.class)
                .when(assertionClientMock)
                .getAssertion(JWT, ASSERTION_REF);

        Assertions.assertThrows(
                LollipopAssertionNotFoundException.class,
                () -> sut.getAssertion(JWT, ASSERTION_REF));

        verify(assertionStorageMock).getAssertion(ASSERTION_REF);
        verify(assertionClientMock).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any(SamlAssertion.class));
    }

    @Test
    void getAssertionWithEmptyJwtParameterFailure()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getAssertion("", ASSERTION_REF));

        verify(assertionStorageMock, never()).getAssertion(anyString());
        verify(assertionClientMock, never()).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any(SamlAssertion.class));
    }

    @Test
    void getAssertionWithNullJwtParameterFailure()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> sut.getAssertion(null, ASSERTION_REF));

        verify(assertionStorageMock, never()).getAssertion(anyString());
        verify(assertionClientMock, never()).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any(SamlAssertion.class));
    }

    @Test
    void getAssertionWithEmptyAssertionRefParameterFailure()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getAssertion(JWT, ""));

        verify(assertionStorageMock, never()).getAssertion(ASSERTION_REF);
        verify(assertionClientMock, never()).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any(SamlAssertion.class));
    }

    @Test
    void getAssertionWithNullAssertionRefParameterFailure()
            throws LollipopAssertionNotFoundException, OidcAssertionNotSupported {
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getAssertion(JWT, null));

        verify(assertionStorageMock, never()).getAssertion(ASSERTION_REF);
        verify(assertionClientMock, never()).getAssertion(JWT, ASSERTION_REF);
        verify(assertionStorageMock, never()).saveAssertion(anyString(), any(SamlAssertion.class));
    }
}

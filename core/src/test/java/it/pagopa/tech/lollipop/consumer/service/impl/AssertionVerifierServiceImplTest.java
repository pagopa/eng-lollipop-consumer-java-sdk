package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssertionVerifierServiceImplTest {

    private IdpCertProvider idpCertProviderMock;
    private AssertionService assertionServiceMock;
    private static LollipopConsumerRequestConfig lollipopRequestConfigMock;

    private AssertionVerifierService sut;

    private static final String VALID_FISCAL_CODE = "AAAAAA89S20I111X";
    private static final String VALID_JWK = "{  \"kty\": \"EC\",  \"crv\": \"P-256\",  \"x\": \"SVqB4JcUD6lsfvqMr-OKUNUphdNn64Eay60978ZlL74\",  \"y\": \"lf0u0pMj4lGAzZix5u4Cm5CMQIgMNpkwy163wtKYVKI\"}";
    private static final String VALID_SHA_256_ASSERTION_REF = "sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg";
    private static final String VALID_SHA_384_ASSERTION_REF = "sha384-lqxC_2kqMdwiBWoD-Us63Fha6e3bE1Y3yUz8G6IJTldohJCIBVDfvS8acB3GJBhw";
    private static final String VALID_SHA_512_ASSERTION_REF = "sha512-nX5CfUc5R-FoYKYZwvQMuc4Tt-heb7vHi_O-AMUSqHNVCw9kNaN2SVuN-DXtGXyUhrcVcQdCyY6FVzl_vyWXNA";

    private static final String EMPTY_ASSERTION_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root></root>";
    private static final String ASSERTION_XML_WITH_INVALID_PERIOD_DATE_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\"><saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\"><saml2:Conditions NotBefore=\"2023-02-28\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\"><saml2:AudienceRestriction><saml2:Audience>https://app-backend.io.italia.it</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions></saml2:Assertion></saml2p:Response>";
    private static final String ASSERTION_XML_WITH_EXPIRED_PERIOD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\"><saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\"><saml2:Conditions NotBefore=\"2000-02-28T16:27:25.400Z\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\"><saml2:AudienceRestriction><saml2:Audience>https://app-backend.io.italia.it</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions></saml2:Assertion></saml2p:Response>";
    private static String ASSERTION_XML_WITHOUT_ATTRIBUTE_TAG;
    private static String ASSERTION_XML_WITHOUT_FISCAL_CODE;
    private static String ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG;
    private static String ASSERTION_XML_WITH_INVALID_INRESPONSETO_ALGORITHM;
    private static String ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM;
    private static String ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA384_ALGORITHM;
    private static String ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA512_ALGORITHM;

    @BeforeAll
    static void prepareInput() {
        lollipopRequestConfigMock = spy(LollipopConsumerRequestConfig.builder().build());

        String todayTimestamp = new SimpleDateFormat(lollipopRequestConfigMock.getAssertionNotBeforeDateFormat()).format(new Date());
        ASSERTION_XML_WITHOUT_ATTRIBUTE_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\"><saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\"><saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\"><saml2:AudienceRestriction><saml2:Audience>https://app-backend.io.italia.it</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions></saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITHOUT_FISCAL_CODE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">      <saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">         <saml2:AudienceRestriction>            <saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>         </saml2:AudienceRestriction>      </saml2:Conditions>\t        <saml2:AttributeStatement>         <saml2:Attribute>         </saml2:Attribute>      </saml2:AttributeStatement>   </saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">      <saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">         <saml2:AudienceRestriction>            <saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>         </saml2:AudienceRestriction>      </saml2:Conditions>\t  <saml2:AttributeStatement>         <saml2:Attribute FriendlyName=\"Codice fiscale\" Name=\"fiscalNumber\">            <saml2:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">TINIT-AAAAAA89S20I111X</saml2:AttributeValue>         </saml2:Attribute>      </saml2:AttributeStatement>   </saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITH_INVALID_INRESPONSETO_ALGORITHM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">\t\t<saml2:Subject>\t\t\t<saml2:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\" NameQualifier=\"https://posteid.poste.it\">SPID-d4de186b-e103-4b39-8209-0bccc7b1acdd</saml2:NameID>\t\t\t<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">\t\t\t\t<saml2:SubjectConfirmationData InResponseTo=\"INVALIDsha-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\" Recipient=\"https://app-backend.io.italia.it/assertionConsumerService\" />\t\t\t</saml2:SubjectConfirmation>\t\t</saml2:Subject>\t\t<saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">\t\t\t<saml2:AudienceRestriction>\t\t\t\t<saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>\t\t\t</saml2:AudienceRestriction>\t\t</saml2:Conditions>\t\t<saml2:AttributeStatement>\t\t\t<saml2:Attribute FriendlyName=\"Codice fiscale\" Name=\"fiscalNumber\">\t\t\t\t<saml2:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">TINIT-AAAAAA89S20I111X</saml2:AttributeValue>\t\t\t</saml2:Attribute>\t\t</saml2:AttributeStatement>\t</saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">\t\t<saml2:Subject>\t\t\t<saml2:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\" NameQualifier=\"https://posteid.poste.it\">SPID-d4de186b-e103-4b39-8209-0bccc7b1acdd</saml2:NameID>\t\t\t<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">\t\t\t\t<saml2:SubjectConfirmationData InResponseTo=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\" Recipient=\"https://app-backend.io.italia.it/assertionConsumerService\" />\t\t\t</saml2:SubjectConfirmation>\t\t</saml2:Subject>\t\t<saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">\t\t\t<saml2:AudienceRestriction>\t\t\t\t<saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>\t\t\t</saml2:AudienceRestriction>\t\t</saml2:Conditions>\t\t<saml2:AttributeStatement>\t\t\t<saml2:Attribute FriendlyName=\"Codice fiscale\" Name=\"fiscalNumber\">\t\t\t\t<saml2:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">TINIT-AAAAAA89S20I111X</saml2:AttributeValue>\t\t\t</saml2:Attribute>\t\t</saml2:AttributeStatement>\t</saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA384_ALGORITHM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha384-lqxC_2kqMdwiBWoD-Us63Fha6e3bE1Y3yUz8G6IJTldohJCIBVDfvS8acB3GJBhw\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">\t\t<saml2:Subject>\t\t\t<saml2:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\" NameQualifier=\"https://posteid.poste.it\">SPID-d4de186b-e103-4b39-8209-0bccc7b1acdd</saml2:NameID>\t\t\t<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">\t\t\t\t<saml2:SubjectConfirmationData InResponseTo=\"sha384-lqxC_2kqMdwiBWoD-Us63Fha6e3bE1Y3yUz8G6IJTldohJCIBVDfvS8acB3GJBhw\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\" Recipient=\"https://app-backend.io.italia.it/assertionConsumerService\" />\t\t\t</saml2:SubjectConfirmation>\t\t</saml2:Subject>\t\t<saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">\t\t\t<saml2:AudienceRestriction>\t\t\t\t<saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>\t\t\t</saml2:AudienceRestriction>\t\t</saml2:Conditions>\t\t<saml2:AttributeStatement>\t\t\t<saml2:Attribute FriendlyName=\"Codice fiscale\" Name=\"fiscalNumber\">\t\t\t\t<saml2:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">TINIT-AAAAAA89S20I111X</saml2:AttributeValue>\t\t\t</saml2:Attribute>\t\t</saml2:AttributeStatement>\t</saml2:Assertion></saml2p:Response>";
        ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA512_ALGORITHM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://app-backend.io.italia.it/assertionConsumerService\" ID=\"_de2ce675-f1e5-46fc-96ed-019803471175\" InResponseTo=\"sha512-nX5CfUc5R-FoYKYZwvQMuc4Tt-heb7vHi_O-AMUSqHNVCw9kNaN2SVuN-DXtGXyUhrcVcQdCyY6FVzl_vyWXNA\" IssueInstant=\"2023-02-28T16:27:26.400Z\" Version=\"2.0\">   <saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\" IssueInstant=\"2023-02-28T16:27:25.400Z\" Version=\"2.0\">\t\t<saml2:Subject>\t\t\t<saml2:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\" NameQualifier=\"https://posteid.poste.it\">SPID-d4de186b-e103-4b39-8209-0bccc7b1acdd</saml2:NameID>\t\t\t<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">\t\t\t\t<saml2:SubjectConfirmationData InResponseTo=\"sha512-nX5CfUc5R-FoYKYZwvQMuc4Tt-heb7vHi_O-AMUSqHNVCw9kNaN2SVuN-DXtGXyUhrcVcQdCyY6FVzl_vyWXNA\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\" Recipient=\"https://app-backend.io.italia.it/assertionConsumerService\" />\t\t\t</saml2:SubjectConfirmation>\t\t</saml2:Subject>\t\t<saml2:Conditions NotBefore=\"" + todayTimestamp + "\" NotOnOrAfter=\"2023-02-28T16:28:25.400Z\">\t\t\t<saml2:AudienceRestriction>\t\t\t\t<saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>\t\t\t</saml2:AudienceRestriction>\t\t</saml2:Conditions>\t\t<saml2:AttributeStatement>\t\t\t<saml2:Attribute FriendlyName=\"Codice fiscale\" Name=\"fiscalNumber\">\t\t\t\t<saml2:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">TINIT-AAAAAA89S20I111X</saml2:AttributeValue>\t\t\t</saml2:Attribute>\t\t</saml2:AttributeStatement>\t</saml2:Assertion></saml2p:Response>";
    }

    @BeforeEach
    void setUp() {
        idpCertProviderMock = mock(IdpCertProvider.class);
        assertionServiceMock = mock(AssertionService.class);

        sut = new AssertionVerifierServiceImpl(idpCertProviderMock, assertionServiceMock, lollipopRequestConfigMock);
    }

    @Test
    void validateLollipopGetAssertionFailureWithOidcAssertionException() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        doThrow(OidcAssertionNotSupported.class).when(assertionServiceMock).getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e = assertThrows(ErrorRetrievingAssertionException.class, () -> sut.validateLollipop(request));

        Assertions.assertTrue(e.getCause() instanceof OidcAssertionNotSupported);
        Assertions.assertEquals(ErrorRetrievingAssertionException.ErrorCode.OIDC_TYPE_NOT_SUPPORTED, e.getErrorCode());
    }

    @Test
    void validateLollipopGetAssertionFailureWithAssertionNotFoundException() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        doThrow(LollipopAssertionNotFoundException.class).when(assertionServiceMock).getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e = assertThrows(ErrorRetrievingAssertionException.class, () -> sut.validateLollipop(request));

        Assertions.assertTrue(e.getCause() instanceof LollipopAssertionNotFoundException);
        Assertions.assertEquals(ErrorRetrievingAssertionException.ErrorCode.SAML_ASSERTION_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void validateLollipopBuildAssertionDocFailure() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData("");

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        ErrorRetrievingAssertionException e = assertThrows(ErrorRetrievingAssertionException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(ErrorRetrievingAssertionException.ErrorCode.ERROR_PARSING_ASSERTION, e.getErrorCode());
    }

    @Test
    void validateLollipopValidatePeriodFailureWithFieldNotFoundInAssertionDoc() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(EMPTY_ASSERTION_XML);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e = assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionPeriodException.ErrorCode.INVALID_ASSERTION_PERIOD, e.getErrorCode());
    }

    @Test
    void validateLollipopValidatePeriodFailureWithInvalidDateFormat() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_PERIOD_DATE_FORMAT);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e = assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionPeriodException.ErrorCode.ERROR_PARSING_ASSERTION_NOT_BEFORE_DATE, e.getErrorCode());
    }

    @Test
    void validateLollipopValidatePeriodFailureWithExpiredAssertion() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_EXPIRED_PERIOD);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionPeriodException e = assertThrows(AssertionPeriodException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionPeriodException.ErrorCode.INVALID_ASSERTION_PERIOD, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateUserIdFailureWithoutAttributeTagInAssertionDoc() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_ATTRIBUTE_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionUserIdException e = assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateUserIdFailureWithoutFiscalCodeInAssertionDoc() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_FISCAL_CODE);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionUserIdException e = assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateUserIdFailureWithInvalidUserIdHeader() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", "");

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionUserIdException e = assertThrows(AssertionUserIdException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionUserIdException.ErrorCode.INVALID_USER_ID, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateThumbprintFailureWithoutSubjectConfirmationDataTagInAssertionDoc() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITHOUT_SUBJECTCONFIRMATIONDATA_TAG);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionThumbprintException e = assertThrows(AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_FIELD_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateThumbprintFailureWithInvalidInResponseToAlgorithm() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", VALID_FISCAL_CODE);


        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_INVALID_INRESPONSETO_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionThumbprintException e = assertThrows(AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_ALGORITHM_NOT_VALID, e.getErrorCode());
    }

    @Test
    void validateLollipopValidateThumbprintFailureWithErrorCalculatingThumbprint() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", "", VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionThumbprintException e = assertThrows(AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionThumbprintException.ErrorCode.ERROR_CALCULATING_ASSERTION_THUMBPRINT, e.getErrorCode());
        Assertions.assertTrue(e.getCause() instanceof ParseException);
    }

    @Test
    void validateLollipopValidateThumbprintFailureWithDifferentAssertionRefAndCalculatedThumbprint() throws OidcAssertionNotSupported, LollipopAssertionNotFoundException {
        LollipopConsumerRequest request = getLollipopConsumerRequest("", VALID_JWK, VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        AssertionThumbprintException e = assertThrows(AssertionThumbprintException.class, () -> sut.validateLollipop(request));

        Assertions.assertEquals(AssertionThumbprintException.ErrorCode.INVALID_IN_RESPONSE_TO, e.getErrorCode());
    }

    @Test
    void validateLollipopSuccessWithSHA256Algorithm() throws LollipopAssertionNotFoundException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, OidcAssertionNotSupported {
        LollipopConsumerRequest request = getLollipopConsumerRequest(VALID_SHA_256_ASSERTION_REF, VALID_JWK, VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA256_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        boolean result = sut.validateLollipop(request);

        Assertions.assertTrue(result);
    }

    @Test
    void validateLollipopSuccessWithSHA384Algorithm() throws LollipopAssertionNotFoundException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, OidcAssertionNotSupported {
        LollipopConsumerRequest request = getLollipopConsumerRequest(VALID_SHA_384_ASSERTION_REF, VALID_JWK, VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA384_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        boolean result = sut.validateLollipop(request);

        Assertions.assertTrue(result);
    }

    @Test
    void validateLollipopSuccessWithSHA512Algorithm() throws LollipopAssertionNotFoundException, AssertionPeriodException, AssertionThumbprintException, AssertionUserIdException, ErrorRetrievingAssertionException, OidcAssertionNotSupported {
        LollipopConsumerRequest request = getLollipopConsumerRequest(VALID_SHA_512_ASSERTION_REF, VALID_JWK, VALID_FISCAL_CODE);

        SamlAssertion assertion = new SamlAssertion();
        assertion.setAssertionData(ASSERTION_XML_WITH_VALID_INRESPONSETO_SHA512_ALGORITHM);

        doReturn(assertion).when(assertionServiceMock).getAssertion(anyString(), anyString());

        boolean result = sut.validateLollipop(request);

        Assertions.assertTrue(result);
    }

    private LollipopConsumerRequest getLollipopConsumerRequest(String assertionRef, String publicKey, String userId) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(lollipopRequestConfigMock.getAuthJWTHeader(), "");
        headers.put(lollipopRequestConfigMock.getAssertionRefHeader(), assertionRef);
        headers.put(lollipopRequestConfigMock.getUserIdHeader(), userId);
        headers.put(lollipopRequestConfigMock.getPublicKeyHeader(), publicKey);

        return LollipopConsumerRequest.builder()
                .headerParams(headers)
                .build();
    }
}
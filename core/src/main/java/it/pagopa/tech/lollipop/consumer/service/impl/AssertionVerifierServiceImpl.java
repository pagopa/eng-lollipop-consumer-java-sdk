/* (C)2023-2025 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64URL;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionRefAlgorithms;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionVerificationResultCode;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.utils.LollipopSamlAssertionWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.saml.SAMLKeyInfo;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Standard implementation of {@link AssertionVerifierService} */
@Slf4j
public class AssertionVerifierServiceImpl implements AssertionVerifierService {

    private final LollipopLoggerService lollipopLoggerService;

    private final IdpCertProvider idpCertProvider;
    private final AssertionService assertionService;
    private final LollipopConsumerRequestConfig lollipopRequestConfig;

    private static final String IN_RESPONSE_TO = "InResponseTo";
    private static final String ISSUE_INSTANT = "IssueInstant";
    private static final String NOT_BEFORE = "NotBefore";

    @Inject
    public AssertionVerifierServiceImpl(
            LollipopLoggerService lollipopLoggerService,
            IdpCertProvider idpCertProvider,
            AssertionService assertionService,
            LollipopConsumerRequestConfig lollipopRequestConfig) {
        this.lollipopLoggerService = lollipopLoggerService;
        this.idpCertProvider = idpCertProvider;
        this.assertionService = assertionService;
        this.lollipopRequestConfig = lollipopRequestConfig;
    }

    /**
     * @see AssertionVerifierService#validateLollipop(LollipopConsumerRequest)
     */
    @Override
    public CommandResult validateLollipop(LollipopConsumerRequest request)
            throws ErrorRetrievingAssertionException, AssertionPeriodException,
                    AssertionThumbprintException, AssertionUserIdException,
                    ErrorRetrievingIdpCertDataException, ErrorValidatingAssertionSignature,
                    AssertionNameException {
        Map<String, String> headerParams = request.getHeaderParams();

        Document assertionDoc =
                getAssertion(
                        headerParams.get(lollipopRequestConfig.getAuthJWTHeader()),
                        headerParams.get(lollipopRequestConfig.getAssertionRefHeader()));

        boolean isAssertionPeriodValid = validateAssertionPeriod(assertionDoc);
        if (!isAssertionPeriodValid) {
            throw new AssertionPeriodException(
                    AssertionPeriodException.ErrorCode.INVALID_ASSERTION_PERIOD,
                    "The assertion has expired");
        }

        boolean isUserIdValid = validateUserId(request, assertionDoc);
        if (!isUserIdValid) {
            throw new AssertionUserIdException(
                    AssertionUserIdException.ErrorCode.INVALID_USER_ID,
                    "The user id in the assertion does not match the request header");
        }

        boolean isInResponseToValid = validateInResponseTo(request, assertionDoc);
        if (!isInResponseToValid) {
            throw new AssertionThumbprintException(
                    AssertionThumbprintException.ErrorCode.INVALID_IN_RESPONSE_TO,
                    "The hash of provided public key do not match the InResponseTo in the"
                            + " assertion");
        }

        List<IdpCertData> idpCertDataList = getIdpCertData(assertionDoc);
        boolean isSignatureValid = validateSignature(assertionDoc, idpCertDataList);
        if (!isSignatureValid) {
            throw new ErrorValidatingAssertionSignature(
                    ErrorValidatingAssertionSignature.ErrorCode.MISSING_ASSERTION_SIGNATURE,
                    "The assetion signature is not valid");
        }
        CommandResult result = validateFullNameHeader(assertionDoc);
        if(result.getName() != null && result.getFamilyName() != null){
            log.info("Il CommandResult non contiene name e familyName");
        }
        // validazione nome e cognome, torna direttamente un CommandResult se tutto è andato ok
        return result;
    }

    private Document getAssertion(String jwt, String assertionRef)
            throws ErrorRetrievingAssertionException {
        SamlAssertion assertion;
        try {
            assertion = assertionService.getAssertion(jwt, assertionRef);
            assertionCheckLogging(
                    "SAML Assertion found using assertionRef {} and jwt {}: {}",
                    assertionRef,
                    jwt,
                    assertion);
        } catch (OidcAssertionNotSupported e) {
            throw new ErrorRetrievingAssertionException(
                    ErrorRetrievingAssertionException.ErrorCode.OIDC_TYPE_NOT_SUPPORTED,
                    e.getMessage(),
                    e);
        } catch (LollipopAssertionNotFoundException e) {
            throw new ErrorRetrievingAssertionException(
                    ErrorRetrievingAssertionException.ErrorCode.SAML_ASSERTION_NOT_FOUND,
                    e.getMessage(),
                    e);
        }
        return buildDocumentFromAssertion(assertion);
    }

    protected boolean validateAssertionPeriod(Document assertionDoc)
            throws AssertionPeriodException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionNotBeforeTag());
        if (isElementNotFound(listElements, NOT_BEFORE)) {
            return false;
        }
        String notBefore =
                listElements.item(0).getAttributes().getNamedItem(NOT_BEFORE).getNodeValue();

        long notBeforeMilliseconds;
        try {
            notBeforeMilliseconds =
                    ISODateTimeFormat.dateTimeParser().parseDateTime(notBefore).getMillis();
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            throw new AssertionPeriodException(
                    AssertionPeriodException.ErrorCode.ERROR_PARSING_ASSERTION_NOT_BEFORE_DATE,
                    e.getMessage(),
                    e);
        }
        long dateNowMilliseconds = new Date().getTime();
        long expiresAfterMilliseconds =
                TimeUnit.DAYS.toMillis(lollipopRequestConfig.getAssertionExpireInDays());
        long dateNowLessNotBefore = (dateNowMilliseconds - notBeforeMilliseconds);

        return 0 <= dateNowLessNotBefore && (dateNowLessNotBefore <= expiresAfterMilliseconds);
    }

    protected boolean validateUserId(LollipopConsumerRequest request, Document assertionDoc)
            throws AssertionUserIdException {
        String userIdHeader =
                request.getHeaderParams().get(lollipopRequestConfig.getUserIdHeader());

        String userIdFromAssertion = getUserIdFromAssertion(assertionDoc);
        if (userIdFromAssertion == null) {
            throw new AssertionUserIdException(
                    AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND,
                    "Missing or invalid Fiscal Code in the retrieved saml assertion.");
        }

        return userIdFromAssertion.equals(userIdHeader);
    }

    protected boolean validateInResponseTo(LollipopConsumerRequest request, Document assertionDoc)
            throws AssertionThumbprintException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionInResponseToTag());
        if (isElementNotFound(listElements, IN_RESPONSE_TO)) {
            throw new AssertionThumbprintException(
                    AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_FIELD_NOT_FOUND,
                    "Missing request id in the retrieved saml assertion");
        }
        String inResponseTo =
                listElements.item(0).getAttributes().getNamedItem(IN_RESPONSE_TO).getNodeValue();

        String inResponseToAlgorithm = retrieveInResponseToAlgorithm(inResponseTo);

        String publicKey =
                request.getHeaderParams().get(lollipopRequestConfig.getPublicKeyHeader());
        String calculatedThumbprint = calculateThumbprint(inResponseToAlgorithm, publicKey);
        String assertionRefHeader =
                request.getHeaderParams().get(lollipopRequestConfig.getAssertionRefHeader());

        return inResponseTo.equals(calculatedThumbprint) && inResponseTo.equals(assertionRefHeader);
    }

    protected List<IdpCertData> getIdpCertData(Document assertionDoc)
            throws ErrorRetrievingIdpCertDataException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionInstantTag());
        if (isElementNotFound(listElements, ISSUE_INSTANT)) {
            throw new ErrorRetrievingIdpCertDataException(
                    ErrorRetrievingIdpCertDataException.ErrorCode.INSTANT_FIELD_NOT_FOUND,
                    "Missing instant field in the retrieved saml assertion");
        }
        String instant =
                listElements.item(0).getAttributes().getNamedItem(ISSUE_INSTANT).getNodeValue();

        String entityId = getEntityId(listElements.item(0).getChildNodes());
        if (entityId == null) {
            throw new ErrorRetrievingIdpCertDataException(
                    ErrorRetrievingIdpCertDataException.ErrorCode.ENTITY_ID_FIELD_NOT_FOUND,
                    "Missing entity id field in the retrieved saml assertion");
        }
        instant = parseInstantToUnixTimestamp(instant);
        try {
            entityId = entityId.trim();
            List<IdpCertData> idpCertData = idpCertProvider.getIdpCertData(instant, entityId);
            this.idpCertDataLogging(
                    "IdP certificates has been found for entityId {} at instant {}: {}",
                    entityId,
                    instant,
                    idpCertData);
            return idpCertData;
        } catch (CertDataNotFoundException e) {
            throw new ErrorRetrievingIdpCertDataException(
                    ErrorRetrievingIdpCertDataException.ErrorCode.IDP_CERT_DATA_NOT_FOUND,
                    "Some error occurred in retrieving certification data from IDP",
                    e);
        }
    }

    protected boolean validateSignature(Document assertionDoc, List<IdpCertData> idpCertDataList)
            throws ErrorValidatingAssertionSignature {
        LollipopSamlAssertionWrapper wrapper;
        try {
            wrapper =
                    new LollipopSamlAssertionWrapper(
                            (Element)
                                    assertionDoc
                                            .getElementsByTagNameNS(
                                                    lollipopRequestConfig
                                                            .getSamlNamespaceAssertion(),
                                                    lollipopRequestConfig.getAssertionInstantTag())
                                            .item(0));
        } catch (WSSecurityException e) {
            throw new ErrorValidatingAssertionSignature(
                    ErrorValidatingAssertionSignature.ErrorCode.ERROR_PARSING_ASSERTION,
                    "Failed to build SAML object from assertion",
                    e);
        }

        return validateSignature(idpCertDataList, wrapper);
    }

    private boolean validateSignature(
            List<IdpCertData> idpCertDataList, LollipopSamlAssertionWrapper wrapper)
            throws ErrorValidatingAssertionSignature {
        for (IdpCertData idpCertData : idpCertDataList) {
            for (String certData : idpCertData.getCertData()) {
                try {
                    X509Certificate x509Certificate = getX509Certificate(certData);
                    wrapper.verifySignatureLollipop(
                            new SAMLKeyInfo(new X509Certificate[] {x509Certificate}));
                } catch (CertificateException | WSSecurityException e) {
                    // CertificateException: Failed to generate X509 certificate from IDP metadata
                    // or
                    // WSSecurityException: Failed to validate assertion signature
                    // this exceptions are ignored because if the signature validation fail for one
                    // certificate it may pass with one of the other certificates
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private X509Certificate getX509Certificate(String idpCertificate) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream fileStream =
                new ByteArrayInputStream(Base64.getMimeDecoder().decode(idpCertificate));
        return (X509Certificate) certificateFactory.generateCertificate(fileStream);
    }

    private static Document buildDocumentFromAssertion(SamlAssertion assertion)
            throws ErrorRetrievingAssertionException {
        String stringXml = assertion.getAssertionData();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(stringXml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ErrorRetrievingAssertionException(
                    ErrorRetrievingAssertionException.ErrorCode.ERROR_PARSING_ASSERTION,
                    e.getMessage(),
                    e);
        }
    }

    private boolean isElementNotFound(NodeList listElements, String elementName) {
        return listElements == null
                || listElements.getLength() <= 0
                || listElements.item(0) == null
                || !listElements.item(0).hasAttributes()
                || listElements.item(0).getAttributes().getNamedItem(elementName) == null
                || listElements.item(0).getAttributes().getNamedItem(elementName).getNodeValue()
                        == null;
    }

    private String getUserIdFromAssertion(Document assertionDoc) throws AssertionUserIdException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionFiscalCodeTag());
        if (listElements == null || listElements.getLength() <= 0) {
            throw new AssertionUserIdException(
                    AssertionUserIdException.ErrorCode.FISCAL_CODE_FIELD_NOT_FOUND,
                    "Missing or invalid Fiscal Code in the retrieved saml assertion.");
        }

        for (int i = 0; i < listElements.getLength(); i++) {
            Node item = listElements.item(i);
            if (item == null || item.getAttributes() == null) {
                continue;
            }
            Node name = item.getAttributes().getNamedItem("Name");
            if (name != null
                    && name.getNodeValue().equals("fiscalNumber")
                    && item.getTextContent() != null) {
                return item.getTextContent().trim().replace("TINIT-", "");
            }
        }
        return null;
    }

    private String retrieveInResponseToAlgorithm(String inResponseTo)
            throws AssertionThumbprintException {
        boolean matchesSHA256 =
                AssertionRefAlgorithms.SHA256.getPattern().matcher(inResponseTo).matches();
        boolean matchesSHA384 =
                AssertionRefAlgorithms.SHA384.getPattern().matcher(inResponseTo).matches();
        boolean matchesSHA512 =
                AssertionRefAlgorithms.SHA512.getPattern().matcher(inResponseTo).matches();

        if (matchesSHA256) {
            return AssertionRefAlgorithms.SHA256.getHashAlgorithm();
        }
        if (matchesSHA384) {
            return AssertionRefAlgorithms.SHA384.getHashAlgorithm();
        }
        if (matchesSHA512) {
            return AssertionRefAlgorithms.SHA512.getHashAlgorithm();
        }
        throw new AssertionThumbprintException(
                AssertionThumbprintException.ErrorCode.IN_RESPONSE_TO_ALGORITHM_NOT_VALID,
                "InResponseTo in the assertion do not contains a valid Assertion Ref or it contains"
                        + " an invalid algorithm.");
    }

    private String calculateThumbprint(String inResponseToAlgorithm, String publicKey)
            throws AssertionThumbprintException {
        Base64URL thumbprint;
        try {
            publicKey = getPublicKey(publicKey);
            thumbprint = ThumbprintUtils.compute(inResponseToAlgorithm, JWK.parse(publicKey));
        } catch (JOSEException | ParseException e) {
            String errMsg = String.format("Can not calculate JwkThumbprint: %S", e.getMessage());
            throw new AssertionThumbprintException(
                    AssertionThumbprintException.ErrorCode.ERROR_CALCULATING_ASSERTION_THUMBPRINT,
                    errMsg,
                    e);
        }
        AssertionRefAlgorithms algo =
                AssertionRefAlgorithms.getAlgorithmFromHash(inResponseToAlgorithm);
        String calculatedThumbprint = String.format("%s-%s", algo.getAlgorithmName(), thumbprint);
        if (!algo.getPattern().matcher(calculatedThumbprint).matches()) {
            throw new AssertionThumbprintException(
                    AssertionThumbprintException.ErrorCode.ERROR_CALCULATING_ASSERTION_THUMBPRINT,
                    "The calculated thumbprint does not match the expected pattern: "
                            + calculatedThumbprint);
        }
        return calculatedThumbprint;
    }

    private String getEntityId(NodeList listElements) {
        if (listElements == null) {
            return null;
        }
        for (int i = 0; i < listElements.getLength(); i++) {
            Node item = listElements.item(i);
            if (item != null
                    && item.getLocalName() != null
                    && item.getLocalName().equals(lollipopRequestConfig.getAssertionEntityIdTag())
                    && item.getTextContent() != null) {
                return item.getTextContent();
            }
        }
        return null;
    }

    private void assertionCheckLogging(String message, Object... args) {
        if (lollipopRequestConfig.isEnableConsumerLogging()
                && lollipopRequestConfig.isEnableAssertionLogging()) {
            lollipopLoggerService.log(message, args);
        }
    }

    private void idpCertDataLogging(String message, Object... args) {
        if (lollipopRequestConfig.isEnableConsumerLogging()
                && lollipopRequestConfig.isEnableIdpCertDataLogging()) {
            lollipopLoggerService.log(message, args);
        }
    }

    private String getPublicKey(String publicKey) {
        try {
            publicKey = new String(Base64.getDecoder().decode(publicKey));
        } catch (Exception e) {
            log.debug("Key not in Base64");
        }
        return publicKey;
    }

    private String parseInstantToUnixTimestamp(String instant) {
        try {
            instant =
                    Long.toString(
                            ISODateTimeFormat.dateTimeParser().parseDateTime(instant).getMillis()
                                    / 1000);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            String msg =
                    String.format(
                            "Retrieved instant %s does not match expected ISO datetime format",
                            instant);
            log.debug(msg);
        }
        return instant;
    }

    protected CommandResult validateFullNameHeader(Document assertionDoc)
            throws AssertionNameException {
        log.info("validateFullNameHeader");

        // name e familyName dall'assertion
        Map<String, String> nameMap = getFullNameFromAssertion(assertionDoc);
        String givenName = nameMap.get("name");
        String familyName = nameMap.get("familyName");
        log.info("Name user from assertion= {} ", givenName);
        log.info("FamilyName user from assertion= {}", familyName);

        CommandResult commandResult = new CommandResult( AssertionVerificationResultCode.ASSERTION_VERIFICATION_SUCCESS.name(),
                "Name and surname successfully validated",
                givenName,
                familyName);
        log.info("CommandResult = {}", commandResult);
        return commandResult;
    }

    private Map<String, String> getFullNameFromAssertion(Document assertionDoc)
            throws AssertionNameException {
        log.info("getFullNameFromAssertion");

        Map<String, String> result = new HashMap<>();

        // recupero del nome (givenName)
        NodeList givenNameElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionNameAndSurnameCodeTag());
        log.info("GivenNameElements {}", givenNameElements.toString());
        if (givenNameElements == null || givenNameElements.getLength() <= 0) {
            throw new AssertionNameException(
                    AssertionNameException.ErrorCode.NAME_NOT_FOUND,
                    "Missing givenName in the retrieved SAML assertion.");
        }

        for (int i = 0; i < givenNameElements.getLength(); i++) {
            Node item = givenNameElements.item(i);
            if (item == null || item.getTextContent() == null) {
                continue;
            }
            Node name = item.getAttributes().getNamedItem("Name");
            if( name != null && name.getNodeValue().equals("name")){
                result.put("name", item.getTextContent().trim());
            }
        }

        // recupero del cognome (familyName)
        NodeList familyNameElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionNameAndSurnameCodeTag());

        if (familyNameElements == null || familyNameElements.getLength() <= 0) {
            throw new AssertionNameException(
                    AssertionNameException.ErrorCode.SURNAME_NOT_FOUND,
                    "Missing familyName in the retrieved SAML assertion.");
        }

        for (int i = 0; i < familyNameElements.getLength(); i++) {
            Node item = familyNameElements.item(i);
            if (item == null || item.getTextContent() == null) {
                continue;
            }
            Node name = item.getAttributes().getNamedItem("Name");
            if(name != null && name.getNodeValue().equals("familyName")) {

                result.put("familyName", item.getTextContent().trim());
            }
        }

        if (!result.containsKey("name") || !result.containsKey("familyName")) {
            throw new AssertionNameException(
                    AssertionNameException.ErrorCode.NAME_OR_SURNAME_NOT_FOUND,
                    "Missing or invalid name/surname in the retrieved SAML assertion.");
        }

        return result;
    }
}

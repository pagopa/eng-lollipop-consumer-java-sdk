/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64URL;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.enumeration.AssertionRefAlgorithms;
import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Slf4j
/** Standard implementation of {@link AssertionVerifierService} */
public class AssertionVerifierServiceImpl implements AssertionVerifierService {

    private final IdpCertProvider idpCertProvider;
    private final AssertionService assertionService;
    private final LollipopConsumerRequestConfig lollipopRequestConfig;

    private static final String IN_RESPONSE_TO = "InResponseTo";

    @Inject
    public AssertionVerifierServiceImpl(
            IdpCertProvider idpCertProvider,
            AssertionService assertionService,
            LollipopConsumerRequestConfig lollipopRequestConfig) {
        this.idpCertProvider = idpCertProvider;
        this.assertionService = assertionService;
        this.lollipopRequestConfig = lollipopRequestConfig;
    }

    /**
     * @see AssertionVerifierService#validateLollipop(LollipopConsumerRequest)
     */
    @Override
    public boolean validateLollipop(LollipopConsumerRequest request)
            throws ErrorRetrievingAssertionException, AssertionPeriodException,
                    AssertionThumbprintException, AssertionUserIdException {
        Map<String, String> headerParams = request.getHeaderParams();

        SamlAssertion assertion =
                getAssertion(
                        headerParams.get(lollipopRequestConfig.getAuthJWTHeader()),
                        headerParams.get(lollipopRequestConfig.getAssertionRefHeader()));
        Document assertionDoc = buildDocumentFromAssertion(assertion);

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

        return true;
    }

    private SamlAssertion getAssertion(String jwt, String assertionRef)
            throws ErrorRetrievingAssertionException {
        try {
            return assertionService.getAssertion(jwt, assertionRef);
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
    }

    private boolean validateAssertionPeriod(Document assertionDoc) throws AssertionPeriodException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionNotBeforeTag());
        if (listElements == null || listElements.getLength() <= 0) {
            return false;
        }
        String notBefore =
                listElements.item(0).getAttributes().getNamedItem("NotBefore").getNodeValue();

        long notBeforeMilliseconds;
        try {
            notBeforeMilliseconds =
                    new SimpleDateFormat(lollipopRequestConfig.getAssertionNotBeforeDateFormat())
                            .parse(notBefore)
                            .getTime();
        } catch (ParseException e) {
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

    private boolean validateUserId(LollipopConsumerRequest request, Document assertionDoc)
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

    private boolean validateInResponseTo(LollipopConsumerRequest request, Document assertionDoc)
            throws AssertionThumbprintException {
        NodeList listElements =
                assertionDoc.getElementsByTagNameNS(
                        lollipopRequestConfig.getSamlNamespaceAssertion(),
                        lollipopRequestConfig.getAssertionInResponseToTag());
        if (isInResponseToFieldFound(listElements)) {
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

    private IdpCertData getIdpCertData(SamlAssertion assertion) {
        return null;
    }

    private boolean validateSignature(SamlAssertion assertion, IdpCertData idpCertData) {
        return false;
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

    private boolean isInResponseToFieldFound(NodeList listElements) {
        return listElements == null
                || listElements.getLength() <= 0
                || listElements.item(0) == null
                || listElements.item(0).getAttributes() == null
                || listElements.item(0).getAttributes().getNamedItem(IN_RESPONSE_TO) == null
                || listElements.item(0).getAttributes().getNamedItem(IN_RESPONSE_TO).getNodeValue()
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

    private String getPublicKey(String publicKey) {
        try {
            publicKey = new String(Base64.getDecoder().decode(publicKey));
        } catch (Exception e) {
            log.debug("Key not in Base64");
        }
        return publicKey;
    }
}

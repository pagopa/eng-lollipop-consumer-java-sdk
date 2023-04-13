/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service.impl;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopValidationException;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AssertionVerifierServiceImpl implements AssertionVerifierService {

    private final IdpCertProvider idpCertProvider;
    private final AssertionService assertionService;
    private final LollipopConsumerRequestConfig lollipopConsumerRequestConfig;


    @Inject
    public AssertionVerifierServiceImpl(IdpCertProvider idpCertProvider, AssertionService assertionService, LollipopConsumerRequestConfig lollipopConsumerRequestConfig) {
        this.idpCertProvider = idpCertProvider;
        this.assertionService = assertionService;
        this.lollipopConsumerRequestConfig = lollipopConsumerRequestConfig;
    }

    @Override
    public boolean validateLollipop(LollipopConsumerRequest request) throws LollipopValidationException {

        Map<String, String> headerParams = request.getHeaderParams();

        String originalMethod = headerParams.get(lollipopConsumerRequestConfig.getOriginalMethodHeader());
        String originalUrl = headerParams.get(lollipopConsumerRequestConfig.getOriginalURLHeader());

        if (!originalMethod.equals(lollipopConsumerRequestConfig.getExpectedFirstLcOriginalMethod()) && !originalUrl.equals(lollipopConsumerRequestConfig.getExpectedFirstLcOriginalUrl())) {
            String errMsg = String.format("Unexpected original method and/or original url: %s, %s", originalMethod, originalUrl);
            throw new LollipopValidationException(LollipopValidationException.ErrorCode.UNEXPECTED_METHOD_OR_URL, errMsg);
        }


        return true;
    }

    private SamlAssertion getAssertion(String jwt, String assertionRef) {
        return null;
    }

    private boolean validateAssertionPeriod(String notBefore) throws ParseException {
        long notBeforeMilliseconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(notBefore).getTime(); //TODO configurazione format date

        long dateNowMilliseconds = new Date().getTime();

        long expiresAfterMilliseconds = TimeUnit.DAYS.toMillis(30); //TODO configurazione giorni scadenza

        long dateNowLessNotBefore = (dateNowMilliseconds - notBeforeMilliseconds);

        if (0 <= dateNowLessNotBefore && (dateNowLessNotBefore <= expiresAfterMilliseconds)) {
            return true;
        }

        return false;
    }

    private boolean validateUserId(LollipopConsumerRequest request, SamlAssertion assertion) throws ParserConfigurationException, IOException, SAXException {
        String stringXml = assertion.getAssertionData();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(stringXml)));

        String userIdHeaders = request.getHeaderParams().get("x-pagopa-lollipop-user-id"); //TODO aggiungere a configurazione

        NodeList listElements = document.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Attribute"); //TODO aggiungere a configurazione
        String userIdAssertion = "";
        if (listElements != null && listElements.getLength() > 0) {
            for (int i = 0; i < listElements.getLength(); i++) {
                String userIdFound = listElements.item(i).getAttributes().getNamedItem("fiscalNumber").getNodeValue();
                if (!userIdFound.isBlank()) {
                    userIdAssertion = userIdFound.trim().replace("TINIT-", "");
                    break;
                }
            }
        }

        return userIdAssertion.equals(userIdHeaders);
    }

    private boolean validateInResponseTo(LollipopConsumerRequest request, SamlAssertion assertion) {
        return false;
    }

    private IdpCertData getIdpCertData(SamlAssertion assertion) {
        return null;
    }

    private boolean validateSignature(SamlAssertion assertion, IdpCertData idpCertData) {
        return false;
    }
}

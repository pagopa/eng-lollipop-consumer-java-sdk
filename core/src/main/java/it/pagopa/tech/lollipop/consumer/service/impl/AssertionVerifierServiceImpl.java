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

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public boolean validateAssertionPeriod(String notBefore) throws ParseException {
        long notBeforeMilliseconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(notBefore).getTime(); //TODO configurazione format date

        long dateNowMilliseconds = new Date().getTime();

        long expiresAfterMilliseconds = TimeUnit.DAYS.toMillis(30); //TODO configurazione giorni scadenza

        long dateNowLessNotBefore = (dateNowMilliseconds - notBeforeMilliseconds);

        if (0 <= dateNowLessNotBefore && (dateNowLessNotBefore <= expiresAfterMilliseconds)) {
            return true;
        }

        return false;
    }

    private boolean validateUserId(LollipopConsumerRequest request, SamlAssertion assertion) {
        return false;
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

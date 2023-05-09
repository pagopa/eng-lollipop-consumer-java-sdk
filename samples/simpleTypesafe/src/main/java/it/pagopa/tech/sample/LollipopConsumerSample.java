package it.pagopa.tech.sample;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.SimpleAssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImpl;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.logger.impl.LollipopLogbackLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import it.pagopa.tech.lollipop.consumer.utils.LollipopTypesafeConfig;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import java.util.HashMap;

import static it.pagopa.tech.sample.LollipopConstants.*;


public class LollipopConsumerSample {

    private static LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    public static void main(String[] args) throws Exception {
        try (ClientAndServer clientAndServer = ClientAndServer.startClientAndServer(3000, 3001);
             MockServerClient mockServerClientAssertion = ClientMocksConfig.createExpectationAssertionFound();
             MockServerClient mockServerClientIdpTag = ClientMocksConfig.createExpectationIdpTagsFound();
             MockServerClient mockServerClientIdpData = ClientMocksConfig.createExpectationIdpDataFound()) {

            LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper = buildLollipopConsumerFactoryHelper();
            LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);

            // Success
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE))
                    .doExecute();

            // Request with invalid content digest
            commandBuilder.createCommand(buildLollipopRequest(
                    INVALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE)).doExecute();

            // Request with invalid message payload
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, INVALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE)).doExecute();

            // Request with invalid encoding
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, INVALID_ENCODING_UTF_326, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE)).doExecute();

            // Request with invalid signature
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    SIGNATURE_INPUT_HEADER_VALUE, INVALID_SIGNATURE_HEADER_VALUE)).doExecute();

            // Success with multi signature
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY,
                    VALID_MULTI_ECDSA_SIGNATURE_INPUT, VALID_MULTI_ECDSA_SIGNATURE)).doExecute();

            // thumbprint validation failed
            commandBuilder.createCommand(buildLollipopRequest(
                    VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD,
                    LOLLIPOP_RSA_PUBKEY, VALID_RSA_PSS_SIGNATURE_INPUT, VALID_RSA_PSS_SIGNATURE)).doExecute();

        }
    }

    private static LollipopConsumerFactoryHelper buildLollipopConsumerFactoryHelper() throws Exception {
        LollipopTypesafeConfig typesafeConfig = new LollipopTypesafeConfig(ConfigFactory.parseResources("application.properties"));
        lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder()
                .assertionNotBeforeDateFormat(typesafeConfig.getLOLLIPOP_ASSERTION_NOT_BEFORE_DATE_FORMAT())
                .assertionInstantDateFormat(typesafeConfig.getLOLLIPOP_ASSERTION_INSTANT_DATE_FORMAT())
                .build();
        HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory(VALID_ENCODING_UTF8,
                LollipopConsumerRequestConfig.builder().build());
        AssertionStorageProvider assertionStorageProvider = new SimpleAssertionStorageProvider();
        IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImpl(
                new IdpCertSimpleClientProvider(IdpCertSimpleClientConfig.builder().baseUri(typesafeConfig.getIDP_CLIENT_BASE_URI()).build(),
                        new SimpleIdpCertStorageProvider(), new IdpCertStorageConfig()));
        AssertionClientProvider assertionClientProvider =
                new AssertionSimpleClientProvider(AssertionSimpleClientConfig.builder().build());
        AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImpl(
                assertionStorageProvider, assertionClientProvider, new StorageConfig());
        LollipopLoggerServiceFactory lollipopLoggerServiceFactory = new LollipopLogbackLoggerServiceFactory();
        return new LollipopConsumerFactoryHelper(lollipopLoggerServiceFactory, messageVerifierFactory, idpCertProviderFactory, assertionServiceFactory,
                new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig), lollipopConsumerRequestConfig);
    }

    private static LollipopConsumerRequest buildLollipopRequest(
            String contentDigest,
            String encoding,
            String payload,
            String lollipopKey,
            String signatureInput,
            String signature) {
        HashMap<String, String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getContentDigestHeader(), contentDigest);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getContentEncodingHeader(), encoding);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getSignatureInputHeader(), signatureInput);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getSignatureHeader(), signature);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getPublicKeyHeader(), lollipopKey);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getOriginalMethodHeader(), lollipopConsumerRequestConfig.getExpectedFirstLcOriginalMethod());
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getOriginalURLHeader(), lollipopConsumerRequestConfig.getExpectedFirstLcOriginalUrl());
        lollipopHeaderParams.put("X-io-sign-qtspclauses","anIoSignClauses");
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getAssertionRefHeader(), VALID_ASSERTION_REF);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getAssertionTypeHeader(), "SAML");
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getAuthJWTHeader(), VALID_JWT);
        lollipopHeaderParams.put(lollipopConsumerRequestConfig.getUserIdHeader(), VALID_FISCAL_CODE);


        return LollipopConsumerRequest.builder()
                .requestBody(payload)
                .headerParams(lollipopHeaderParams)
                .build();
    }
}

package it.pagopa.tech.sample;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.*;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImpl;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;

import java.util.HashMap;
import java.util.logging.Logger;

import static it.pagopa.tech.sample.LollipopConstants.*;


public class LollipopConsumerSample {

    public static void main(String[] args) throws Exception {
        LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(buildLollipopConsumerFactoryHelper());

        CommandResult commandResult =  commandBuilder.createCommand(buildLollipopRequest(
                VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE))
                .doExecute();
        //Logger.getGlobal().info(buildMessage(commandResult, "Validation of a valid Lollipop request ended with status code: "));

//        commandResult = command.doExecute(buildLollipopRequest(
//                INVALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a Lollipop request with invalid digest header ended with status code: "));
//
//        commandResult = command.doExecute(buildLollipopRequest(
//                VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, INVALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a Lollipop request with invalid content ended with status code: "));
//
//        commandResult = command.doExecute(buildLollipopRequest(
//                VALID_CONTENT_DIGEST, INVALID_ENCODING_UTF_326, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, SIGNATURE_INPUT_HEADER_VALUE, SIGNATURE_HEADER_VALUE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a Lollipop request with unsupported encoding ended with status code: "));
//
//        commandResult = command.doExecute(buildLollipopRequest(
//                VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, SIGNATURE_INPUT_HEADER_VALUE, INVALID_SIGNATURE_HEADER_VALUE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a Lollipop request with invalid signature ended with status code: "));
//
//        commandResult = command.doExecute(buildLollipopRequest(
//                VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, ECDSA_LOLLIPOP_JWT_KEY, VALID_MULTI_ECDSA_IGNATURE_INPUT, VALID_MULTI_ECDSA_SIGNATURE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a valid Lollipop request with ecdsa multi-signature ended with status code: "));
//
//        commandResult = command.doExecute(buildLollipopRequest(
//                VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD, LOLLIPOP_RSA_PUBKEY, VALID_RSA_PSS_SIGNATURE_INPUT, VALID_RSA_PSS_SIGNATURE));
//        Logger.getGlobal().info(buildMessage(commandResult, "Validation of a valid Lollipop request with rsa-pss-256 signature ended with status code: "));

    }

    private static LollipopConsumerFactoryHelper buildLollipopConsumerFactoryHelper() throws Exception {
        LollipopConsumerRequestConfig lollipopConsumerRequestConfig = new LollipopConsumerRequestConfig();
        HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory(VALID_ENCODING_UTF8,
                LollipopConsumerRequestConfig.builder().build());
        AssertionStorageProvider assertionStorageProvider = new SimpleAssertionStorageProvider();
        IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImpl(
                new IdpCertSimpleClientProvider(IdpCertSimpleClientConfig.builder().build()));
        AssertionClientProvider assertionClientProvider =
                new AssertionSimpleClientProvider(AssertionSimpleClientConfig.builder().build());
        AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImpl(
                assertionStorageProvider, assertionClientProvider, new StorageConfig());
        return new LollipopConsumerFactoryHelper(messageVerifierFactory, idpCertProviderFactory, assertionServiceFactory,
                new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig));
    }

    private static LollipopConsumerRequest buildLollipopRequest(
            String contentDigest,
            String encoding,
            String payload,
            String lollipopKey,
            String signatureInput,
            String signature) {
        HashMap<String, String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put(CONTENT_DIGEST, contentDigest);
        lollipopHeaderParams.put(CONTENT_ENCODING, encoding);
        lollipopHeaderParams.put(SIGNATURE_INPUT, signatureInput);
        lollipopHeaderParams.put(SIGNATURE, signature);
        lollipopHeaderParams.put(LOLLIPOP_KEY, lollipopKey);
        lollipopHeaderParams.put(LOLLIPOP_ORIGIN_METHOD, EXPECTED_ORIGIN_METHOD);
        lollipopHeaderParams.put(LOLLIPOP_ORIGIN_URL, EXCPECTED_ORIGIN_URL);
        lollipopHeaderParams.put("X-io-sign-qtspclauses","anIoSignClauses");


        return LollipopConsumerRequest.builder()
                .requestBody(payload)
                .headerParams(lollipopHeaderParams)
                .build();
    }

    private static String buildMessage(CommandResult commandResult, String s) {
        return s + commandResult.getResultCode() + " and message: " + commandResult.getResultMessage();
    }
}

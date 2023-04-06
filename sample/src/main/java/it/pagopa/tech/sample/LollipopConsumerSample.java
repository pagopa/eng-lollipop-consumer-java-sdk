package it.pagopa.tech.sample;

import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImplStub;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImplStub;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

import java.util.HashMap;

import static it.pagopa.tech.sample.Constants.*;

public class LollipopConsumerSample {

    public static void main(String[] args) throws Exception {
        LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(buildLollipopConsumerFactoryHelper());
        LollipopConsumerCommand command = commandBuilder.createCommand();

        CommandResult commandResult = command.doExecute(buildLollipopRequest(VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD));
        System.out.println(buildMessage(commandResult, "Validation of a valid Lollipop request ended with status code "));

        commandResult = command.doExecute(buildLollipopRequest(INVALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, VALID_MESSAGE_PAYLOAD));
        System.out.println(buildMessage(commandResult, "Validation of a Lollipop request with invalid digest header ended with status code "));

        commandResult = command.doExecute(buildLollipopRequest(VALID_CONTENT_DIGEST, VALID_ENCODING_UTF8, INVALID_MESSAGE_PAYLOAD));
        System.out.println(buildMessage(commandResult, "Validation of a Lollipop request with invalid content ended with status code "));

        commandResult = command.doExecute(buildLollipopRequest(VALID_CONTENT_DIGEST, INVALID_ENCODING_UTF_326, VALID_MESSAGE_PAYLOAD));
        System.out.println(buildMessage(commandResult, "Validation of a Lollipop request with unsupported encoding ended with status code "));
    }

    private static LollipopConsumerFactoryHelper buildLollipopConsumerFactoryHelper() throws Exception {
        HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory(VALID_ENCODING_UTF8);
        IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImplStub();
        AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImplStub();
        return new LollipopConsumerFactoryHelper(messageVerifierFactory, idpCertProviderFactory, assertionServiceFactory);
    }

    private static LollipopConsumerRequest buildLollipopRequest(String contentDigest, String encoding, String payload) {
        HashMap<String, String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put(CONTENT_DIGEST, contentDigest);
        lollipopHeaderParams.put(CONTENT_ENCODING, encoding);
        lollipopHeaderParams.put(SIGNATURE_INPUT, SIGNATURE_INPUT_HEADER_VALUE);
        lollipopHeaderParams.put(SIGNATURE, SIGNATURE_HEADER_VALUE);

        return LollipopConsumerRequest.builder()
                .requestBody(payload)
                .headerParams(lollipopHeaderParams)
                .build();
    }

    private static String buildMessage(CommandResult commandResult, String s) {
        return s + commandResult.getResultCode() + " and message " + commandResult.getResultMessage();
    }
}

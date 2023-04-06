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

public class LollipopConsumerSample {

    private static final String ENCODING = "UTF-8";

    public static void main(String[] args) throws Exception {
        LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(buildLollipopConsumerFactoryHelper());
        LollipopConsumerCommand command = commandBuilder.createCommand();
        CommandResult commandResult = command.doExecute(buildLollipopRequest());
        System.out.println("Validation ended with status code " + commandResult.getResultCode() + " and message " + commandResult.getResultMessage());
    }

    private static LollipopConsumerFactoryHelper buildLollipopConsumerFactoryHelper() throws Exception {
        HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory(ENCODING);
        IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImplStub();
        AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImplStub();
        return new LollipopConsumerFactoryHelper(messageVerifierFactory, idpCertProviderFactory, assertionServiceFactory);
    }

    private static LollipopConsumerRequest buildLollipopRequest() {
        HashMap<String, String> lollipopHeaderParams = new HashMap<>();
        lollipopHeaderParams.put("content-digest", "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:");
        lollipopHeaderParams.put("content-encoding", ENCODING);
        lollipopHeaderParams.put(
                "signature-input",
                "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\" "
                        + "\"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid="
                        + "\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg");
        lollipopHeaderParams.put(
                "signature",
                "sig1=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:");

        return LollipopConsumerRequest.builder()
                .requestBody("{\"message\":\"a valid message payload\"}")
                .headerParams(lollipopHeaderParams)
                .build();
    }
}

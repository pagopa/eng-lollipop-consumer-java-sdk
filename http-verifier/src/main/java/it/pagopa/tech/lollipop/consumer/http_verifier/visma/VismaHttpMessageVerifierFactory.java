/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import java.nio.charset.Charset;

/** Implements {@link HttpMessageVerifierFactory} with Visma-AutoPay http-signature library */
public class VismaHttpMessageVerifierFactory implements HttpMessageVerifierFactory {

    private final String defaultEncoding;

    public VismaHttpMessageVerifierFactory(String defaultEncoding) throws Exception {
        if (Charset.availableCharsets().get(defaultEncoding) == null) {
            throw new Exception("Unavailable Encoding: " + defaultEncoding);
        }
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * {@inheritDoc}
     *
     * @return instance of {@link VismaHttpMessageVerifierFactory}, passing the configured default
     *     encoding
     */
    @Override
    public HttpMessageVerifier create() {
        return new VismaHttpMessageVerifier(defaultEncoding);
    }
}
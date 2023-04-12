/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import static org.assertj.core.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import net.visma.autopay.http.digest.DigestException;
import org.junit.jupiter.api.Test;

public class VismaHttpMessageVerifierTest {

    public VismaHttpMessageVerifier vismaDigestVerifier = new VismaHttpMessageVerifier("UTF-8",
            LollipopConsumerRequestConfig.builder().build());

    @Test
    public void correctDigestIsVerified() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header =
                "md5=:V9tg6T+1JldSH4+Zy8c5jw==:,sha-256=:1LKaloxAFzY43tjRdMhpV6+iEb5HnO4CDbpd/hJ9kco=:";

        // execute & verify
        assertThatNoException()
                .isThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null));
    }

    @Test
    public void invalidDigestIsDetected() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "sha-256=:A5BYxvLAy0ksUzsKTRTvd8wPeKvMztUofYShogEc+4E=:";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e.getCause()).hasMessageContaining("different");
                            assertThat(((DigestException) e.getCause()).getErrorCode())
                                    .isEqualTo(DigestException.ErrorCode.INCORRECT_DIGEST);
                        });
    }

    @Test
    public void malformedDigestIsDetected() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "sha-256=1LKaloxAFzY43tjRdMhpV6+iEb5HnO4CDbpd/hJ9kco=";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e.getCause()).hasMessageContaining("parsing");
                            assertThat(((DigestException) e.getCause()).getErrorCode())
                                    .isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
                        });
    }

    @Test
    public void unsupportedAlgorithmsAreDetected() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "md5=:V9tg6T+1JldSH4+Zy8c5jw==: ,sha=:q3kRUT3rxwFa1QQpqBWXcUWLJM4=:";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e.getCause()).hasMessageContaining("Unsupported");
                            assertThat(((DigestException) e.getCause()).getErrorCode())
                                    .isEqualTo(DigestException.ErrorCode.UNSUPPORTED_ALGORITHM);
                        });
    }

    @Test
    public void emptyHeaderIsDetected() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("Empty");
                            assertThat(((DigestException) e.getCause()).getErrorCode())
                                    .isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
                        });
    }

    @Test
    public void invalidDictionaryValuesAreDetected() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "sha-256=ok";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(
                        LollipopDigestException.class,
                        e -> {
                            assertThat(e.getCause()).hasMessageContaining("Invalid");
                            assertThat(((DigestException) e.getCause()).getErrorCode())
                                    .isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
                        });
    }

    @Test
    public void invalidContentEncoding() {
        // setup
        var content = new String(new byte[] {1, 2, 4});
        var header = "sha-256=ok";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, "UTF-326"))
                .isInstanceOf(UnsupportedEncodingException.class);
    }

    @Test
    public void validSignatureTest() {

        String signatureInput = "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\" \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature = "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";


        Map<String,String> requestHeaders = Map.of(
                "Content-Digest", "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:",
                "x-pagopa-lollipop-original-url", "https://api-app.io.pagopa.it/first-lollipop/sign",
                "x-pagopa-lollipop-original-method", "POST",
                "x-pagopa-lollipop-public-key","eyJrdHkiOiJFQyIsIngiOiJGcUZEd" +
                        "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im" +
                        "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO" +
                        "iJQLTI1NiJ9",
                "Signature-Input", signatureInput,
                "Signature", signature
        );

        // execute & verify
        assertThatNoException()
                .isThrownBy(() -> vismaDigestVerifier.verifyHttpSignature(
                        signature, signatureInput, requestHeaders));

    }

}

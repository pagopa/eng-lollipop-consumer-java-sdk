/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import static org.assertj.core.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import it.pagopa.tech.lollipop.consumer.exception.LollipopSignatureException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import net.visma.autopay.http.digest.DigestException;
import org.junit.jupiter.api.Test;

public class VismaHttpMessageVerifierTest {

    public VismaHttpMessageVerifier vismaDigestVerifier =
            new VismaHttpMessageVerifier("UTF-8", LollipopConsumerRequestConfig.builder().build());

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
    public void validLollipopSignatureCheckSingleEcdaSha256() {

        String signatureInput =
                "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "Content-Digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                    + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                    + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                    + "iJQLTI1NiJ9",
                                "Signature-Input",
                                signatureInput,
                                "Signature",
                                signature));

        // execute & verify
        assertThatNoException()
                .isThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders));
    }

    @Test
    public void invalidLollipopSignatureCheck() {

        String signatureInput =
                "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "Content-Digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Za=:",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                    + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                    + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                    + "iJQLTI1NiJ9",
                                "Signature-Input",
                                signatureInput,
                                "Signature",
                                signature));

        // execute & verify
        assertThatThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders))
                .isInstanceOfSatisfying(
                        LollipopSignatureException.class,
                        e -> {
                            assertThat(e).hasMessageContaining("The provided signature is invalid");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(
                                            LollipopSignatureException.ErrorCode.INVALID_SIGNATURE);
                        });
    }

    @Test
    public void validLollipopSignatureCheckSingleRsaSha256() {

        String signatureInput =
                "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678814391;nonce=\"aNonce\";"
                    + "alg=\"rsa-pss-sha256\";keyid=\"sha256-A3OhKGLYwSvdJ2txHi_SGQ3G-sHLh2Ibu91ErqFx_58\"";
        var signature =
                "sig1=:Jf7v1wqk4bWDZzS0aqbA8VIYxBD07KkrhVmf8ncqsCCpgtggKzVpuwzsxJGDaxqw1sQ/4/9q3JviW7cV0Iq1EbFPiX"
                    + "kW9j9F+JPNt+pPZCjTrcHzKSZ+Yz+MYttSS/umR0YdCPdkObu28HyZ1hcTgt2xSqyYpjxX9CPcjHn42tVJBF6Kfmxn"
                    + "AdcYH3vjFj30QPRyMUjQEH9FEQItcxP7H4P9vXsHsKi2o3NFwgl8Lq5zCOMURbM4BtgxJwVh97MJzqPVJEq3isEa60h"
                    + "quPIdIjPoL9tgMEZkbERHZzqg3KivS9cjdQ7VsWWdwu8S2mPbRVK7SAyhEpk+hnmpxg24Uw==:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "Content-Digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Za=:",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsImtpZCI6InRlc3Qta2V5LXJzYS1wc3MiLCJuIjoicjR0bW0zc"
                                    + "jIwV2RfUGJxdlAxczItUUV0dnB1UmFWOFlxNDBnalVSOHkyUmp4YTZkcEcyR1hIYlBmdk0gIHM4Y3Q"
                                    + "tTGgxR0g0NXgyOFJ3M1J5NTNtbS1vQVhqeVE4Nk9uRGtaNU44bFliZ2dENE8zdzZNNnBBdkxraGs5NU"
                                    + "FuICBkVHJpZmJJRlBOVThQUE1PN095ckZBSHFnRHN6bmpQRm1UT3RDRWNOMloxRnBXZ2Nod3VZTFBMLV"
                                    + "dva3FsdGQxMSAgbnFxemktYko5Y3ZTS0FEWWRVQUFONVdVdHpkcGl5NkxiVGdTeFA3b2NpVTRUbjBnNU"
                                    + "k2YURaSjdBOEx6bzBLU3kgIFpZb0E0ODVtcWNPMEdWQWRWdzlscTRhT1Q5djZkLW5iNGJuTmtRVmtsTFE"
                                    + "zZlZBdkptLXhkRE9wOUxDTkNONDhWICAycG5ET2tGVjYtVTluVjVveWM2WEkydyJ9",
                                "Signature-Input",
                                signatureInput,
                                "Signature",
                                signature));

        // execute & verify
        assertThatNoException()
                .isThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders));
    }

    @Test
    public void validLollipopMultipleSignatureCheckEcdaSha256() {

        String signatureInput =
                "sig1=(\"x-io-sign-qtspclauses\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\","
                    + " sig2=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig1=:dncsEeKERA9wzxBO0vbPIueMK7Izk4zZNX4D0jI+t17XQJ5YrhumR3MGvMiyarb+B8MPqn+rbOJwZt6dV+oXFA==:,"
                    + " sig2=:nbmFduqX8AdhXzqkFX+UIvicn3ZV5yZXqUO+3bceOT8WFPXRTVRcoOcjF+0+W5KLihAZjSW5GXSgCxVVEW8pqQ==:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "X-io-sign-qtspclauses",
                                "anIoSignClauses",
                                "Content-Digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                    + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                    + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                    + "iJQLTI1NiJ9"));

        // execute & verify
        assertThatNoException()
                .isThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders));
    }

    @Test
    public void invalidLollipopMultipleSignatureWithLessInput() {

        String signatureInput =
                "sig1=(\"x-io-sign-qtspclauses\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\","
                    + " sig2=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
        var signature =
                "sig1=:dncsEeKERA9wzxBO0vbPIueMK7Izk4zZNX4D0jI+t17XQJ5YrhumR3MGvMiyarb+B8MPqn+rbOJwZt6dV+oXFA==:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "X-io-sign-qtspclauses",
                                "anIoSignClauses",
                                "Content-Digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
                                    + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
                                    + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
                                    + "iJQLTI1NiJ9"));

        // execute & verify
        assertThatThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders))
                .isInstanceOfSatisfying(
                        LollipopSignatureException.class,
                        e -> {
                            assertThat(e)
                                    .hasMessageContaining(
                                            "Available signatures and signature-inputs differ in"
                                                    + " number");
                            assertThat(e.getErrorCode())
                                    .isEqualTo(
                                            LollipopSignatureException.ErrorCode
                                                    .INVALID_SIGNATURE_NUMBER);
                        });
    }

    @Test
    public void validLollipopSignatureCheckSingleEcdaSha256WithDer() {

        String signatureInput =
                "sig1=(\"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1681473980;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-HiNolL87UYKQfaKISwIzyWY4swKPUzpaOWJCxaHy89M\"";
        var signature =
                "sig1=:MEUCIFiZHxuLhk2Jlt46E5kbB8hCx7fN7QeeAj2gaSK3Y+WzAiEAtggj3Jwu8RbTGdNmsDix2zymh0gKwKxoPlolL7j6VTg=:";

        Map<String, String> requestHeaders =
                new HashMap<>(
                        Map.of(
                                "x-pagopa-lollipop-assertion-ref",
                                "sha256-HiNolL87UYKQfaKISwIzyWY4swKPUzpaOWJCxaHy89M",
                                "x-pagopa-lollipop-assertion-type",
                                "SAML",
                                "x-pagopa-lollipop-user-id",
                                "aFiscalCode",
                                "x-pagopa-lollipop-public-key",
                                "eyJrdHkiOiJFQyIsInkiOiJNdkVCMENsUHFnTlhrNVhIYm9xN1hZUnE2TnJTQkFTVmZhT2wzWnAxQmJzPSIsImNydiI6IlAtMjU2IiwieCI6InF6YTQzdGtLTnIrYWlTZFdNL0Q1cTdxMElmV3lZVUFIVEhSNng3dFByZEU9In0",
                                "x-pagopa-lollipop-auth-jwt",
                                "aValidJWT",
                                "x-pagopa-lollipop-original-method",
                                "POST",
                                "x-pagopa-lollipop-original-url",
                                "https://api-app.io.pagopa.it/first-lollipop/sign",
                                "content-digest",
                                "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:",
                                "Signature-Input",
                                signatureInput,
                                "Signature",
                                signature));

        // execute & verify
        assertThatNoException()
                .isThrownBy(
                        () ->
                                vismaDigestVerifier.verifyHttpSignature(
                                        signature, signatureInput, requestHeaders));
    }
}

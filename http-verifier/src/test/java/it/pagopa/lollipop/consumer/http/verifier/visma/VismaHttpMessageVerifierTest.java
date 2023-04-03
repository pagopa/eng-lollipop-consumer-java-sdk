/*
 * Copyright (c) 2022 Visma Autopay AS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package it.pagopa.lollipop.consumer.http.verifier.visma;

import it.pagopa.common.lollipop.consumer.exception.LollipopDigestException;
import net.visma.autopay.http.digest.DigestException;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.*;

class VismaHttpMessageVerifierTest {

    public VismaHttpMessageVerifier vismaDigestVerifier = new VismaHttpMessageVerifier();

    @Test
    void correctDigestIsVerified() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "md5=:V9tg6T+1JldSH4+Zy8c5jw==:,sha-256=:1LKaloxAFzY43tjRdMhpV6+iEb5HnO4CDbpd/hJ9kco=:";

        // execute & verify
        assertThatNoException().isThrownBy(() -> vismaDigestVerifier
                .verifyDigest(header, content, null));
    }

    @Test
    void invalidDigestIsDetected() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "sha-256=:A5BYxvLAy0ksUzsKTRTvd8wPeKvMztUofYShogEc+4E=:";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content,null))
                .isInstanceOfSatisfying(LollipopDigestException.class, e -> {
            assertThat(e.getCause()).hasMessageContaining("different");
            assertThat(((DigestException) e.getCause())
                    .getErrorCode()).isEqualTo(DigestException.ErrorCode.INCORRECT_DIGEST);
        });
    }

    @Test
    void malformedDigestIsDetected() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "sha-256=1LKaloxAFzY43tjRdMhpV6+iEb5HnO4CDbpd/hJ9kco=";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(LollipopDigestException.class, e -> {
            assertThat(e.getCause()).hasMessageContaining("parsing");
            assertThat(((DigestException) e.getCause())
                    .getErrorCode()).isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
        });
    }

    @Test
    void unsupportedAlgorithmsAreDetected() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "md5=:V9tg6T+1JldSH4+Zy8c5jw==: ,sha=:q3kRUT3rxwFa1QQpqBWXcUWLJM4=:";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(LollipopDigestException.class, e -> {
            assertThat(e.getCause()).hasMessageContaining("Unsupported");
            assertThat(((DigestException) e.getCause())
                    .getErrorCode()).isEqualTo(DigestException.ErrorCode.UNSUPPORTED_ALGORITHM);
        });
    }

    @Test
    void emptyHeaderIsDetected() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(LollipopDigestException.class, e -> {
            assertThat(e).hasMessageContaining("Empty");
            assertThat(((DigestException) e.getCause())
                    .getErrorCode()).isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
        });
    }

    @Test
    void invalidDictionaryValuesAreDetected() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "sha-256=ok";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, null))
                .isInstanceOfSatisfying(LollipopDigestException.class, e -> {
            assertThat(e.getCause()).hasMessageContaining("Invalid");
            assertThat(((DigestException) e.getCause())
                    .getErrorCode()).isEqualTo(DigestException.ErrorCode.INVALID_HEADER);
        });
    }

    @Test
    void invalidContentEncoding() {
        // setup
        var content = new String(new byte[]{1, 2, 4});
        var header = "sha-256=ok";

        // execute & verify
        assertThatThrownBy(() -> vismaDigestVerifier.verifyDigest(header, content, "UTF-326"))
                .isInstanceOf(UnsupportedEncodingException.class);
    }

}
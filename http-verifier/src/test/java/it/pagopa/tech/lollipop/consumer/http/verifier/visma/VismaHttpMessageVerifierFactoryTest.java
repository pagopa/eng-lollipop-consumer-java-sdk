package it.pagopa.tech.lollipop.consumer.http.verifier.visma;

import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class VismaHttpMessageVerifierFactoryTest {

    @Test
    public void encodingInCostructorIsInvalid() {
        assertThatThrownBy(() -> new VismaHttpMessageVerifierFactory("UTF-326"))
                .isInstanceOfSatisfying(Exception.class, e ->
                        assertThat(e).hasMessageContaining("Unavailable Encoding: UTF-326"));
    }

    @Test
    public void encodingInCostructorIsValid() {
        assertThatNoException().isThrownBy(
                () -> new VismaHttpMessageVerifierFactory("UTF-8"));
    }

    @SneakyThrows
    @Test
    public void instanceIsCreated() {
        assertThat(new VismaHttpMessageVerifierFactory("UTF-8").create())
                .isInstanceOf(VismaHttpMessageVerifier.class);
    }

}
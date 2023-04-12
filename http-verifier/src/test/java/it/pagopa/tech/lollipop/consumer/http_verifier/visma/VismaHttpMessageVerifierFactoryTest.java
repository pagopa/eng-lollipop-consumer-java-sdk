/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import static org.assertj.core.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class VismaHttpMessageVerifierFactoryTest {

    LollipopConsumerRequestConfig lollipopConsumerRequestConfig;

    @BeforeAll
    public void init() {
        lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder().build();
    }

    @Test
    public void encodingInCostructorIsInvalid() {
        assertThatThrownBy(() -> new VismaHttpMessageVerifierFactory("UTF-326",lollipopConsumerRequestConfig))
                .isInstanceOfSatisfying(
                        Exception.class,
                        e -> assertThat(e).hasMessageContaining("Unavailable Encoding: UTF-326"));
    }

    @Test
    public void encodingInCostructorIsValid() {
        assertThatNoException().isThrownBy(() -> new VismaHttpMessageVerifierFactory("UTF-8",lollipopConsumerRequestConfig));
    }

    @SneakyThrows
    @Test
    public void instanceIsCreated() {
        assertThat(new VismaHttpMessageVerifierFactory("UTF-8",lollipopConsumerRequestConfig).create())
                .isInstanceOf(VismaHttpMessageVerifier.class);
    }
}

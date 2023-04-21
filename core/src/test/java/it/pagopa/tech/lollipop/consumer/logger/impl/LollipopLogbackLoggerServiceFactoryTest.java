/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.logger.impl;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class LollipopLogbackLoggerServiceFactoryTest {

    @SneakyThrows
    @Test
    void instanceIsCreated() {
        assertThat(new LollipopLogbackLoggerServiceFactory().create())
                .isInstanceOf(LollipopLogbackLoggerService.class);
    }
}

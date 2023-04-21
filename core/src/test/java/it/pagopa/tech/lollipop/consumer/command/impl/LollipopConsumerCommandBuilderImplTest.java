/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LollipopConsumerCommandBuilderImplTest {

    LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder;

    @BeforeEach
    void beforeAll() {
        LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper =
                Mockito.mock(LollipopConsumerFactoryHelper.class);
        lollipopConsumerCommandBuilder =
                new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
    }

    @Test
    public void testThatCreatsCommand() {
        assertThat(
                        lollipopConsumerCommandBuilder.createCommand(
                                LollipopConsumerRequest.builder().build()))
                .isInstanceOf(LollipopConsumerCommand.class);
    }
}

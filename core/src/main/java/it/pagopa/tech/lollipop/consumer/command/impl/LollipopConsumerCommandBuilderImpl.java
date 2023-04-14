/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import javax.inject.Inject;

/**
 * Implementation of {@link LollipopConsumerCommandBuilder}, used to create instances of {@link
 * LollipopConsumerCommandImpl}
 */
public class LollipopConsumerCommandBuilderImpl implements LollipopConsumerCommandBuilder {

    private final LollipopConsumerFactoryHelper factoryHelper;

    @Inject
    public LollipopConsumerCommandBuilderImpl(LollipopConsumerFactoryHelper factoryHelper) {
        this.factoryHelper = factoryHelper;
    }

    /**
     * Builder for creating an instance of {@link LollipopConsumerCommand}
     *
     * @return an instance of {@link LollipopConsumerCommand}
     */
    @Override
    public LollipopConsumerCommand createCommand() {
        return new LollipopConsumerCommandImpl(
                factoryHelper.getHttpMessageVerifierService(),
                factoryHelper.getAssertionVerifierService(),
                factoryHelper.getRequestValidationService()
        );
    }
}

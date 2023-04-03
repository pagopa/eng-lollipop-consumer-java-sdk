package it.pagopa.common.lollipop.consumer.command.impl;

import it.pagopa.common.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.common.lollipop.consumer.model.CommandResult;
import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;

public class LollipopConsumerCommandImplStub implements LollipopConsumerCommand {
    @Override
    public CommandResult doExecute(LollipopConsumerRequest request) {
        return null;
    }
}

package it.pagopa.tech.lollipop.consumer.command.impl;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

public class LollipopConsumerCommandImplStub implements LollipopConsumerCommand {
    @Override
    public CommandResult doExecute(LollipopConsumerRequest request) {
        return null;
    }
}

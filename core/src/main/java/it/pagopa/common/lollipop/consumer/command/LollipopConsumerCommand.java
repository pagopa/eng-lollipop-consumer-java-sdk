package it.pagopa.common.lollipop.consumer.command;

import it.pagopa.common.lollipop.consumer.model.CommandResult;
import it.pagopa.common.lollipop.consumer.model.LollipopConsumerRequest;

public interface LollipopConsumerCommand {

    CommandResult doExecute(LollipopConsumerRequest request);
}

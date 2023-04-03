package it.pagopa.spid.common.lc.command;

import it.pagopa.spid.common.lc.model.CommandResult;
import it.pagopa.spid.common.lc.model.LollipopConsumerRequest;

public interface LollipopConsumerCommand {

    CommandResult doExecute(LollipopConsumerRequest request);
}

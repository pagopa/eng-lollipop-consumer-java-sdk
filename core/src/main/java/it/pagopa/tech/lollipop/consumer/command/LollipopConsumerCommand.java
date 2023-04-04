/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command;

import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

public interface LollipopConsumerCommand {

    CommandResult doExecute(LollipopConsumerRequest request);
}

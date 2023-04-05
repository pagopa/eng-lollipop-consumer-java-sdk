/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command;

import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

/**
 *  Interface for the command executing the lollipop request consumption
 * */
public interface LollipopConsumerCommand {

    /**
     * Command that execute all necessary method for validating a Lollipop request: HTTP message
     * verification and Saml assertion verification
     *
     * @param request LolliPop request
     * @return {@link CommandResult} object with result code and message of request verification
     */
    CommandResult doExecute(LollipopConsumerRequest request);
}

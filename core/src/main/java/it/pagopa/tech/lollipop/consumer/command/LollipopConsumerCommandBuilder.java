/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.command;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

/** Builder class for creating command instance */
public interface LollipopConsumerCommandBuilder {

    /**
     * Builder for creating an instance of {@link LollipopConsumerCommand}
     *
     * @return an instance of {@link LollipopConsumerCommand}
     */
    LollipopConsumerCommand createCommand(LollipopConsumerRequest lollipopConsumerRequest);
}

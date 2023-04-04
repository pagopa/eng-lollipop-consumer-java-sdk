/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.service;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;

public interface AssertionVerifierService {
    boolean validateLollipop(LollipopConsumerRequest request);
}

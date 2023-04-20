package it.pagopa.tech.lollipop.consumer.logger.impl;

import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;

public class LollipopLogbackLoggerServiceFactory implements LollipopLoggerServiceFactory {
    @Override
    public LollipopLoggerService create() {
        return new LollipopLogbackLoggerService();
    }

}

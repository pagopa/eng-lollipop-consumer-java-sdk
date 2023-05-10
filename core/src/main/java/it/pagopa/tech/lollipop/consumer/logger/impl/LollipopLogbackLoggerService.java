/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.logger.impl;

import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LollipopLogbackLoggerService implements LollipopLoggerService {

    @Override
    public void log(String message, Object... args) {
        log.info("LollipopAudit: " + message, args);
    }
}

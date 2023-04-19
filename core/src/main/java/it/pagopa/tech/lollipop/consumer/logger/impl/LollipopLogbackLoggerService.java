package it.pagopa.tech.lollipop.consumer.logger.impl;

import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

@Slf4j
public class LollipopLogbackLoggerService implements LollipopLoggerService {

    @Override
    public void log(Level level, String message, Object... args) {
        log.atLevel(level).log(message, args);
    }

}

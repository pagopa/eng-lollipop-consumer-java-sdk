package it.pagopa.tech.lollipop.consumer.logger;

import org.slf4j.event.Level;

public interface LollipopLoggerService {

    public void log(Level level, String message, Object... args);

}

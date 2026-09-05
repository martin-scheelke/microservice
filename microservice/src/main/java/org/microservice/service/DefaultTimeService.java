package org.microservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.microservice.config.DataAccessProperties;
import org.microservice.data.TimeRepository;
import org.microservice.domain.TimeReading;

/**
 * Default {@link TimeService}. Delegates to whichever {@link TimeRepository}
 * implementation the configuration activated.
 */
@Service
public class DefaultTimeService implements TimeService {

    private static final Logger log = LoggerFactory.getLogger(DefaultTimeService.class);

    private final TimeRepository timeRepository;

    public DefaultTimeService(TimeRepository timeRepository, DataAccessProperties properties) {
        this.timeRepository = timeRepository;
        log.info("Time service using {} data-access layer ({})",
                properties.getAccessMode(), timeRepository.getClass().getSimpleName());
    }

    @Override
    public TimeReading currentTime() {
        log.info("Reading current time via {}", timeRepository.getClass().getSimpleName());
        TimeReading reading = timeRepository.readCurrentTime();
        log.info("Read current time {} (accessMode={}, accessLogId={})",
                reading.time(), reading.accessMode(), reading.accessLogId());
        return reading;
    }
}

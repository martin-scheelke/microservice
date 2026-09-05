package org.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.test.config.DataAccessProperties;
import org.test.data.TimeRepository;
import org.test.domain.TimeReading;

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
        return timeRepository.readCurrentTime();
    }
}

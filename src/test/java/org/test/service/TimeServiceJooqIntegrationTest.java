package org.test.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.test.data.TimeRepository;
import org.test.data.jooq.JooqTimeRepository;
import org.test.domain.AccessMode;
import org.test.domain.TimeReading;

/**
 * Integration test for the service layer wired to the real Spring context with
 * the default (jOOQ) data-access layer active. Unlike {@link DefaultTimeServiceTest},
 * the DB layer is not mocked: the service reads the current time through
 * {@link JooqTimeRepository} against the in-memory H2 database.
 */
@SpringBootTest
class TimeServiceJooqIntegrationTest {

    @Autowired
    TimeService timeService;

    @Autowired
    TimeRepository timeRepository;

    @Test
    void jooqRepositoryIsActive() {
        assertThat(timeRepository).isInstanceOf(JooqTimeRepository.class);
    }

    @Test
    void currentTimeIsReadThroughJooqLayerAndAudited() {
        long before = timeRepository.accessLogCount();

        TimeReading reading = timeService.currentTime();

        assertThat(reading.accessMode()).isEqualTo(AccessMode.JOOQ);
        assertThat(reading.time()).isNotNull();
        assertThat(reading.accessLogId()).isPositive();
        assertThat(timeRepository.accessLogCount()).isEqualTo(before + 1);
    }
}

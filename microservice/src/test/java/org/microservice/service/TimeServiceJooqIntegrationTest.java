package org.microservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.microservice.data.TimeRepository;
import org.microservice.data.jooq.JooqTimeRepository;
import org.microservice.domain.AccessMode;
import org.microservice.domain.TimeReading;
import org.microservice.support.PostgresTestcontainersConfig;

/**
 * Integration test for the service layer wired to the real Spring context with
 * the default (jOOQ) data-access layer active. Unlike {@link DefaultTimeServiceTest},
 * the DB layer is not mocked: the service reads the current time through
 * {@link JooqTimeRepository} against a real PostgreSQL container.
 */
@SpringBootTest
@Import(PostgresTestcontainersConfig.class)
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

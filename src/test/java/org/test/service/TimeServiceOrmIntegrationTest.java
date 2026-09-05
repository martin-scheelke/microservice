package org.test.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.test.data.TimeRepository;
import org.test.data.orm.OrmTimeRepository;
import org.test.domain.AccessMode;
import org.test.domain.TimeReading;

/**
 * Integration test for the service layer wired to the real Spring context with
 * the JPA/ORM data-access layer active via {@code app.db.access-mode=orm}.
 * Unlike {@link DefaultTimeServiceTest}, the DB layer is not mocked: the service
 * reads the current time through {@link OrmTimeRepository} against the in-memory
 * H2 database.
 */
@SpringBootTest(properties = "app.db.access-mode=orm")
class TimeServiceOrmIntegrationTest {

    @Autowired
    TimeService timeService;

    @Autowired
    TimeRepository timeRepository;

    @Test
    void ormRepositoryIsActive() {
        assertThat(timeRepository).isInstanceOf(OrmTimeRepository.class);
    }

    @Test
    void currentTimeIsReadThroughOrmLayerAndAudited() {
        long before = timeRepository.accessLogCount();

        TimeReading reading = timeService.currentTime();

        assertThat(reading.accessMode()).isEqualTo(AccessMode.ORM);
        assertThat(reading.time()).isNotNull();
        assertThat(reading.accessLogId()).isPositive();
        assertThat(timeRepository.accessLogCount()).isEqualTo(before + 1);
    }
}

package org.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.config.DataAccessProperties;
import org.test.data.TimeRepository;
import org.test.domain.AccessMode;
import org.test.domain.TimeReading;

/**
 * Unit test for the service layer. No Spring context, DB layer mocked.
 */
@ExtendWith(MockitoExtension.class)
class DefaultTimeServiceTest {

    @Mock
    TimeRepository timeRepository;

    @Mock
    DataAccessProperties dataAccessProperties;

    @InjectMocks
    DefaultTimeService service;

    @Test
    void delegatesCurrentTimeToRepository() {
        TimeReading expected = new TimeReading(
                OffsetDateTime.of(2026, 9, 2, 10, 15, 30, 0, ZoneOffset.UTC), AccessMode.JOOQ, 7L);
        when(timeRepository.readCurrentTime()).thenReturn(expected);

        TimeReading actual = service.currentTime();

        assertThat(actual).isEqualTo(expected);
        verify(timeRepository).readCurrentTime();
    }
}

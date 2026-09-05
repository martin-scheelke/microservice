package org.microservice.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.microservice.domain.AccessMode;
import org.microservice.domain.TimeReading;
import org.microservice.service.TimeService;

/**
 * Controller-layer slice test. Service layer mocked, no DB.
 */
@WebMvcTest(TimeController.class)
class TimeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TimeService timeService;

    @Test
    void returnsCurrentTimePayload() throws Exception {
        when(timeService.currentTime()).thenReturn(new TimeReading(
                OffsetDateTime.of(2026, 9, 2, 10, 15, 30, 0, ZoneOffset.UTC), AccessMode.JOOQ, 42L));

        mockMvc.perform(get("/api/v1/time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTime").value("2026-09-02T10:15:30Z"))
                .andExpect(jsonPath("$.accessMode").value("jooq"))
                .andExpect(jsonPath("$.accessLogId").value(42));
    }
}

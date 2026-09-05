package org.test.client;

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

/**
 * Controller-layer slice test. {@link TimeClient} mocked, no outbound HTTP call.
 */
@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TimeClient timeClient;

    @Test
    void returnsCurrentTimeFromClient() throws Exception {
        when(timeClient.getCurrentTime()).thenReturn(new TimeResponse(
                OffsetDateTime.of(2026, 9, 2, 10, 15, 30, 0, ZoneOffset.UTC), "jooq", 42L));

        mockMvc.perform(get("/client/time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTime").value("2026-09-02T10:15:30Z"))
                .andExpect(jsonPath("$.accessMode").value("jooq"))
                .andExpect(jsonPath("$.accessLogId").value(42));
    }
}

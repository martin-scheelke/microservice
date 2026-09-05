package org.test.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.test.api.TimeApi;
import org.test.domain.TimeReading;
import org.test.model.TimeResponse;
import org.test.service.TimeService;

/**
 * Controller layer. Implements the OpenAPI-generated {@link TimeApi} interface
 * and maps the domain {@link TimeReading} onto the generated {@link TimeResponse}.
 */
@RestController
public class TimeController implements TimeApi {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @Override
    public ResponseEntity<TimeResponse> getCurrentTime() {
        TimeReading reading = timeService.currentTime();

        TimeResponse body = new TimeResponse(
                reading.time(),
                TimeResponse.AccessModeEnum.fromValue(reading.accessMode().wireValue()))
                .accessLogId(reading.accessLogId());

        return ResponseEntity.ok(body);
    }
}

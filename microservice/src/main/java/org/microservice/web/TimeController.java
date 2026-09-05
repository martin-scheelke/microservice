package org.microservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.microservice.api.TimeApi;
import org.microservice.domain.TimeReading;
import org.microservice.model.TimeResponse;
import org.microservice.service.TimeService;

/**
 * Controller layer. Implements the OpenAPI-generated {@link TimeApi} interface
 * and maps the domain {@link TimeReading} onto the generated {@link TimeResponse}.
 */
@RestController
public class TimeController implements TimeApi {

    private static final Logger log = LoggerFactory.getLogger(TimeController.class);

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @Override
    public ResponseEntity<TimeResponse> getCurrentTime() {
        log.info("GET /api/v1/time received");

        TimeReading reading = timeService.currentTime();

        TimeResponse body = new TimeResponse(
                reading.time(),
                TimeResponse.AccessModeEnum.fromValue(reading.accessMode().wireValue()))
                .accessLogId(reading.accessLogId());

        log.info("Returning current time {} (accessMode={}, accessLogId={})",
                reading.time(), reading.accessMode(), reading.accessLogId());

        return ResponseEntity.ok(body);
    }
}

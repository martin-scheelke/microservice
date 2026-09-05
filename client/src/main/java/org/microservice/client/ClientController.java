package org.microservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo endpoint showing the client consuming the time microservice.
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final TimeClient timeClient;

    public ClientController(TimeClient timeClient) {
        this.timeClient = timeClient;
    }

    @GetMapping("/time")
    public TimeResponse currentTime() {
        log.info("GET /client/time received");
        TimeResponse response = timeClient.getCurrentTime();
        log.info("Returning current time {} (accessMode={}, accessLogId={})",
                response.currentTime(), response.accessMode(), response.accessLogId());
        return response;
    }
}

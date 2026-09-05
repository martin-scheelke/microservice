package org.test.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo endpoint showing the client consuming the time microservice.
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    private final TimeClient timeClient;

    public ClientController(TimeClient timeClient) {
        this.timeClient = timeClient;
    }

    @GetMapping("/time")
    public TimeResponse currentTime() {
        return timeClient.getCurrentTime();
    }
}

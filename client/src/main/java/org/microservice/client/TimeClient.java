package org.microservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Talks to the time microservice's {@code GET /api/v1/time} endpoint.
 */
@Component
public class TimeClient {

    private final RestClient restClient;

    public TimeClient(RestClient.Builder builder, TimeClientProperties properties) {
        this.restClient = builder.baseUrl(properties.getBaseUrl()).build();
    }

    public TimeResponse getCurrentTime() {
        return restClient.get()
                .uri("/api/v1/time")
                .retrieve()
                .body(TimeResponse.class);
    }
}

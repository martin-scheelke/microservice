package org.microservice.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Full-context load test. {@link org.microservice.client.ClientControllerTest} mocks
 * {@link TimeClient}, so it never exercises real bean wiring; this catches missing
 * autoconfiguration (e.g. RestClient.Builder) that only surfaces when the app actually starts.
 */
@SpringBootTest
class ClientApplicationContextTest {

    @Autowired
    TimeClient timeClient;

    @Test
    void contextLoadsAndWiresTimeClient() {
        assertThat(timeClient).isNotNull();
    }
}

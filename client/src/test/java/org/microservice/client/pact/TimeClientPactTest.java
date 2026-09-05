package org.microservice.client.pact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestClient;
import org.microservice.client.TimeClient;
import org.microservice.client.TimeClientProperties;
import org.microservice.client.TimeResponse;

/**
 * Consumer-driven contract test. Pact spins up its own local, in-process mock
 * HTTP server that plays the role of the provider for the duration of each test
 * method: no separately-run mock server or real microservice is needed here.
 * Running this test also writes the pact file (the contract) that
 * {@code TimeApiPactVerificationTest} on the provider side later verifies against.
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "time-microservice", pactVersion = PactSpecVersion.V4)
class TimeClientPactTest {

    @Pact(consumer = "time-client")
    V4Pact currentTimePact(PactDslWithProvider builder) {
        return builder
                .given("the time service is available")
                .uponReceiving("a request for the current time")
                    .path("/api/v1/time")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(Map.of("Content-Type", "application/json"))
                    .body(new PactDslJsonBody()
                            .stringMatcher("currentTime", "\\d{4}-\\d{2}-\\d{2}T.*", "2026-09-02T10:15:30Z")
                            .stringMatcher("accessMode", "jooq|orm", "jooq")
                            .numberType("accessLogId", 42))
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "currentTimePact")
    void clientParsesCurrentTimeFromProvider(MockServer mockServer) {
        TimeClientProperties properties = new TimeClientProperties();
        properties.setBaseUrl(mockServer.getUrl());
        TimeClient client = new TimeClient(RestClient.builder(), properties);

        TimeResponse response = client.getCurrentTime();

        assertThat(response.currentTime()).isNotNull();
        assertThat(response.accessMode()).isIn("jooq", "orm");
        assertThat(response.accessLogId()).isPositive();
    }
}

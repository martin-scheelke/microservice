package org.microservice.web.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.microservice.support.PostgresTestcontainersConfig;

/**
 * Provider-side verification of the pact(s) written by {@code TimeClientPactTest}
 * in the {@code time-client} module. Runs the real Spring Boot application
 * (with a real PostgreSQL container behind it, via {@link PostgresTestcontainersConfig})
 * and replays each recorded interaction against it — no mock server involved on
 * this side, since the provider itself is the thing under test.
 */
@Provider("time-microservice")
@PactFolder("pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestcontainersConfig.class)
class TimeApiPactVerificationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setTarget(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("the time service is available")
    void timeServiceIsAvailable() {
        // The time endpoint has no preconditions: it is always available.
    }
}

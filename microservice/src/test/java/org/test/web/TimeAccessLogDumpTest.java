package org.test.web;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.test.data.TimeRepository;
import org.test.support.PostgresTestcontainersConfig;

/**
 * Calls the time endpoint several times, then dumps the whole
 * {@code time_access_log} table so the accumulated audit rows can be inspected.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestcontainersConfig.class)
class TimeAccessLogDumpTest {

    private static final Logger log = LoggerFactory.getLogger(TimeAccessLogDumpTest.class);
    private static final int CALLS = 5;

    @LocalServerPort
    int port;

    @Autowired
    TimeRepository timeRepository;

    @Autowired
    DSLContext dsl;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void callsTimeEndpointRepeatedlyThenDumpsTable() {
        long before = timeRepository.accessLogCount();

        for (int i = 1; i <= CALLS; i++) {
            given()
                .accept(ContentType.JSON)
            .when()
                .get("/api/v1/time")
            .then()
                .statusCode(200)
                .body("accessMode", equalTo("jooq"))
                .body("accessLogId", equalTo((int) before + i));
        }

        Result<?> rows = dsl.fetch("SELECT id, access_mode, recorded_at FROM time_access_log ORDER BY id");

        log.info("time_access_log contents ({} rows):\n{}", rows.size(), rows.format());
        System.out.println("time_access_log contents (" + rows.size() + " rows):");
        System.out.println(rows.format());

        assertThat(timeRepository.accessLogCount()).isEqualTo(before + CALLS);
        assertThat(rows).hasSize((int) before + CALLS);
    }
}

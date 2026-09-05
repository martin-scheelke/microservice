package org.test.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.test.data.TimeRepository;
import org.test.data.jooq.JooqTimeRepository;
import org.test.support.PostgresTestcontainersConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.assertj.core.api.Assertions;

/**
 * Full-stack REST Assured test against the running app with the default
 * (jOOQ) data-access layer.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestcontainersConfig.class)
class TimeApiJooqRestAssuredTest {

    @LocalServerPort
    int port;

    @Autowired
    TimeRepository timeRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void jooqRepositoryIsActive() {
        Assertions.assertThat(timeRepository).isInstanceOf(JooqTimeRepository.class);
    }

    @Test
    void getCurrentTimeReturnsTimeFromJooqLayer() {
        long before = timeRepository.accessLogCount();

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/time")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("currentTime", matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*"))
            .body("currentTime", notNullValue())
            .body("accessMode", equalTo("jooq"))
            .body("accessLogId", greaterThan(0));

        Assertions.assertThat(timeRepository.accessLogCount()).isEqualTo(before + 1);
    }
}

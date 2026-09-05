package org.test.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.test.data.TimeRepository;
import org.test.data.orm.OrmTimeRepository;

/**
 * Full-stack REST Assured test with the app switched to the JPA/ORM
 * data-access layer via {@code app.db.access-mode=orm}.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.db.access-mode=orm")
class TimeApiOrmRestAssuredTest {

    @LocalServerPort
    int port;

    @Autowired
    TimeRepository timeRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void ormRepositoryIsActive() {
        Assertions.assertThat(timeRepository).isInstanceOf(OrmTimeRepository.class);
    }

    @Test
    void getCurrentTimeReturnsTimeFromOrmLayer() {
        long before = timeRepository.accessLogCount();

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/time")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("currentTime", matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*"))
            .body("accessMode", equalTo("orm"))
            .body("accessLogId", greaterThan(0));

        Assertions.assertThat(timeRepository.accessLogCount()).isEqualTo(before + 1);
    }
}

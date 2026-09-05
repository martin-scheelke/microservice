package org.microservice.data.orm;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.microservice.data.TimeRepository;
import org.microservice.domain.AccessMode;
import org.microservice.domain.TimeReading;

/**
 * JPA/ORM implementation of the DB layer. Active when {@code app.db.access-mode=orm}.
 * Reads the database clock through the JPA {@link EntityManager} and writes the
 * audit row through a Spring Data repository / mapped entity.
 */
@Repository
@ConditionalOnProperty(name = "app.db.access-mode", havingValue = "orm")
public class OrmTimeRepository implements TimeRepository {

    private final TimeAccessLogJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public OrmTimeRepository(TimeAccessLogJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TimeReading readCurrentTime() {
        Object raw = entityManager.createNativeQuery("SELECT CURRENT_TIMESTAMP").getSingleResult();
        OffsetDateTime now = toOffsetDateTime(raw);

        TimeAccessLog saved = jpaRepository.save(new TimeAccessLog(AccessMode.ORM.wireValue(), now));
        return new TimeReading(now, AccessMode.ORM, saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long accessLogCount() {
        return jpaRepository.count();
    }

    private static OffsetDateTime toOffsetDateTime(Object raw) {
        return switch (raw) {
            case OffsetDateTime odt -> odt;
            case Instant instant -> instant.atOffset(ZoneOffset.UTC);
            case Timestamp ts -> ts.toInstant().atOffset(ZoneOffset.UTC);
            case LocalDateTime ldt -> ldt.atOffset(ZoneOffset.UTC);
            case null -> throw new IllegalStateException("Database returned no current timestamp");
            default -> throw new IllegalStateException(
                    "Unexpected timestamp type from database: " + raw.getClass());
        };
    }
}

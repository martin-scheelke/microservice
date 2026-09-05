package org.test.data.jooq;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.test.data.TimeRepository;
import org.test.domain.AccessMode;
import org.test.domain.TimeReading;

/**
 * jOOQ implementation of the DB layer. Active when {@code app.db.access-mode=jooq}
 * (the default). Uses the jOOQ DSL directly against the {@code time_access_log}
 * table declared in {@code schema.sql} — no generated jOOQ code.
 */
@Repository
@ConditionalOnProperty(name = "app.db.access-mode", havingValue = "jooq", matchIfMissing = true)
public class JooqTimeRepository implements TimeRepository {

    private static final Table<?> TIME_ACCESS_LOG = DSL.table("time_access_log");
    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<String> ACCESS_MODE = DSL.field("access_mode", String.class);
    private static final Field<OffsetDateTime> RECORDED_AT = DSL.field("recorded_at", OffsetDateTime.class);

    private final DSLContext dsl;

    public JooqTimeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public TimeReading readCurrentTime() {
        OffsetDateTime now = dsl.select(DSL.currentOffsetDateTime())
                .fetchOne(0, OffsetDateTime.class);

        Long id = dsl.insertInto(TIME_ACCESS_LOG)
                .set(ACCESS_MODE, AccessMode.JOOQ.wireValue())
                .set(RECORDED_AT, now)
                .returningResult(ID)
                .fetchOne(ID);

        return new TimeReading(now, AccessMode.JOOQ, id);
    }

    @Override
    @Transactional(readOnly = true)
    public long accessLogCount() {
        return dsl.fetchCount(TIME_ACCESS_LOG);
    }
}

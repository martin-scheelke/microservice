package org.test.data;

import org.test.domain.TimeReading;

/**
 * DB layer. Reads the current time from the in-memory database and records an
 * audit row. Implemented twice: once with jOOQ, once with JPA/ORM. Exactly one
 * implementation is active, chosen by {@code app.db.access-mode}.
 */
public interface TimeRepository {

    TimeReading readCurrentTime();

    /** Number of audit rows written so far (used by tests). */
    long accessLogCount();
}

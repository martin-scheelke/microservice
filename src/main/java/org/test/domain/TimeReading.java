package org.test.domain;

import java.time.OffsetDateTime;

/**
 * Result of a current-time read: the time reported by the in-memory database,
 * the data-access layer that produced it, and the id of the audit row written.
 */
public record TimeReading(OffsetDateTime time, AccessMode accessMode, long accessLogId) {
}

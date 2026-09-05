package org.test.data.orm;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity mapped to the {@code time_access_log} audit table.
 */
@Entity
@Table(name = "time_access_log")
public class TimeAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_mode", nullable = false, length = 16)
    private String accessMode;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    protected TimeAccessLog() {
        // for JPA
    }

    public TimeAccessLog(String accessMode, OffsetDateTime recordedAt) {
        this.accessMode = accessMode;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public OffsetDateTime getRecordedAt() {
        return recordedAt;
    }
}

package org.test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.test.domain.AccessMode;

/**
 * Binds {@code app.db.*}. The {@code access-mode} value also drives the
 * {@code @ConditionalOnProperty} switch that picks the jOOQ or the ORM repository.
 */
@ConfigurationProperties(prefix = "app.db")
public class DataAccessProperties {

    /** Which data-access layer to activate: {@code jooq} or {@code orm}. */
    private AccessMode accessMode = AccessMode.JOOQ;

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }
}

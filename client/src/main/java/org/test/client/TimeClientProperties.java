package org.test.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code time-client.*}.
 */
@ConfigurationProperties(prefix = "time-client")
public class TimeClientProperties {

    /** Base URL of the time microservice. */
    private String baseUrl = "http://localhost:8080";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

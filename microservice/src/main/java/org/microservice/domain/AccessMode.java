package org.microservice.domain;

/**
 * Selects which data-access layer serves time reads.
 */
public enum AccessMode {

    JOOQ,
    ORM;

    /** Lower-case wire form used in the API contract and the audit table. */
    public String wireValue() {
        return name().toLowerCase();
    }
}

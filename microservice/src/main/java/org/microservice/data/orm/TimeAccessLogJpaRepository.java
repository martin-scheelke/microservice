package org.microservice.data.orm;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the audit table.
 */
public interface TimeAccessLogJpaRepository extends JpaRepository<TimeAccessLog, Long> {
}

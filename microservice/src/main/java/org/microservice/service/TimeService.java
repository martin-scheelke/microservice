package org.microservice.service;

import org.microservice.domain.TimeReading;

/**
 * Service layer. Sits between the controller and the DB layer.
 */
public interface TimeService {

    TimeReading currentTime();
}

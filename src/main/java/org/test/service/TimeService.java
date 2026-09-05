package org.test.service;

import org.test.domain.TimeReading;

/**
 * Service layer. Sits between the controller and the DB layer.
 */
public interface TimeService {

    TimeReading currentTime();
}

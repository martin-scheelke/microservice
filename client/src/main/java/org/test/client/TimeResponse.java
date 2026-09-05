package org.test.client;

import java.time.OffsetDateTime;

/**
 * Consumer-owned view of the time microservice's {@code TimeResponse} payload.
 * Deliberately independent of the provider's generated model: a consumer should
 * only depend on the fields it actually uses, which is also what the Pact
 * contract in {@code TimeClientPactTest} pins down.
 */
public record TimeResponse(OffsetDateTime currentTime, String accessMode, Long accessLogId) {
}

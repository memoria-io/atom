package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

public record TPConfig(TP tp,
                       StorageType storageType,
                       int streamReplication,
                       long fetchWaitMillis,
                       int fetchBatchSize,
                       boolean denyDelete,
                       boolean denyPurge) {

}

package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

public record TopicConfig(Topic topic,
                          StorageType storageType,
                          int streamReplication,
                          long fetchWaitMillis,
                          int fetchBatchSize,
                          boolean denyDelete,
                          boolean denyPurge) {

}

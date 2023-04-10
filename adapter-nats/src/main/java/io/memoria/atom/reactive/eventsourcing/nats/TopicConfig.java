package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

public record TopicConfig(Topic topic,
                          StorageType storageType,
                          int streamReplication,
                          long fetchWaitMillis,
                          boolean denyDelete,
                          boolean denyPurge) {

  static TopicConfig appendOnly(Topic topic, StorageType storageType, int streamReplication, long fetchWaitMillis) {
    return new TopicConfig(topic, storageType, streamReplication, fetchWaitMillis, true, true);
  }
}

package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

public class TestUtils {
  private TestUtils() {}

  public static TopicConfig streamConfig(String topic, int partitions) {
    var tp = Topic.create(topic, partitions);
    return new TopicConfig(tp, StorageType.File, 1, 1000, 100, true, true);
  }
}

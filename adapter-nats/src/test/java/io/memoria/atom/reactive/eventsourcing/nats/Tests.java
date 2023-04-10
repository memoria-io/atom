package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

import java.time.Duration;

public class Tests {
  private Tests() {}

  public static TopicConfig topicConfig(String topicName, int partitions) {
    var topic = Topic.create(topicName, partitions);
    return TopicConfig.appendOnly(topic, StorageType.File, 1, 256, Duration.ofMillis(500), 5, 10);
  }
}

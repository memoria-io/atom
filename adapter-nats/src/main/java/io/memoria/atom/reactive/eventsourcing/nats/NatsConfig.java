package io.memoria.atom.reactive.eventsourcing.nats;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public record NatsConfig(String url, Set<TopicConfig> topics) {

  public NatsConfig(String url, TopicConfig... topic) {
    this(url, HashSet.of(topic));
  }

  public Option<TopicConfig> find(String name) {
    return topics.find(tp -> tp.topic().topic().equals(name));
  }
}

package io.memoria.atom.reactive.eventsourcing.nats;

import io.vavr.collection.Set;
import io.vavr.control.Option;

public record NatsConfig(String url, Set<TopicConfig> topics) {
  static final long DEFAULT_FETCH_WAIT = 1000L;

  public Option<TopicConfig> find(String name) {
    return topics.find(tp -> tp.topic().topic().equals(name));
  }
}

package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.control.Try;

import java.util.Map;
import java.util.stream.Stream;

public interface ESStream {
  Try<ESStreamMsg> pub(ESStreamMsg esStreamMsg);

  Stream<ESStreamMsg> sub(String topic, int partition, int maxWaitMillis);

  /**
   * @return an in memory ESStream
   */
  static ESStream inMemory(Map<String, Integer> topicPartitions) {
    return new MemESStream(topicPartitions);
  }

  /**
   * @return an in memory ESStream
   */
  static ESStream inMemory(String topic, int totalPartitions) {
    return new MemESStream(topic, totalPartitions);
  }
}
package io.memoria.atom.active.eventsourcing.stream;

import io.vavr.control.Try;

import java.util.Map;
import java.util.stream.Stream;

public interface ESStream {
  Try<ESStreamMsg> pub(ESStreamMsg esStreamMsg);

  Stream<ESStreamMsg> sub(String topic, int partition);

  /**
   * @return an in memory ESStream
   */
  static ESStream create(Map<String, Integer> topicPartitions) {
    return new MemESStream(topicPartitions);
  }

  /**
   * @return an in memory ESStream
   */
  static ESStream create(String topic, int totalPartitions) {
    return new MemESStream(topic, totalPartitions);
  }
}
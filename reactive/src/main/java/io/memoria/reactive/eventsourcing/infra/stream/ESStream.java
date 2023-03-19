package io.memoria.reactive.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ESStream {
  Mono<ESStreamMsg> pub(ESStreamMsg esStreamMsg);

  Flux<ESStreamMsg> sub(String topic, int partition);

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

package io.memoria.atom.core.eventsourcing.pipeline.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ESMsgStream {
  Mono<ESMsg> pub(ESMsg esMsg);

  Flux<ESMsg> sub(String topic, int partition);

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(Map<String, Integer> topicPartitions) {
    return new MemESMsgStream(topicPartitions);
  }

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(String topic, int totalPartitions) {
    return new MemESMsgStream(topic, totalPartitions);
  }
}

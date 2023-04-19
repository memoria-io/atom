package io.memoria.atom.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ESMsgStream {
  Mono<ESMsg> pub(ESMsg esMsg);

  Flux<ESMsg> sub(String topic, int partition);

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(String topic, int totalPartitions) {
    return new MemESMsgStream(Map.of(topic, totalPartitions));
  }

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(Map<String, Integer> topics) {
    return new MemESMsgStream(topics);
  }

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(Map<String, Integer> topics, int capacity) {
    return new MemESMsgStream(topics, capacity);
  }
}

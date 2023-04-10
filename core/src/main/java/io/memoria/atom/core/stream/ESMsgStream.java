package io.memoria.atom.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

public interface ESMsgStream extends AutoCloseable{
  Mono<ESMsg> pub(ESMsg esMsg);

  Flux<ESMsg> sub(String topic, int partition);

  /**
   *
   * @param topic
   * @param partition
   * @param maxWait before returning an empty result
   * @return
   */
  Mono<ESMsg> getLast(String topic, int partition, Duration maxWait);

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

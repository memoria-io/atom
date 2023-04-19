package io.memoria.atom.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ESMsgStream {
  Mono<ESMsg> pub(ESMsg esMsg);

  Flux<ESMsg> sub(String topic, int partition);

  /**
   * @return an in memory ESStream
   */
  static ESMsgStream inMemory(int totalPartition, String... topics) {
    return new MemESMsgStream(totalPartition, topics);
  }
}

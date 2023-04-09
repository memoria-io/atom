package io.memoria.atom.eventsourcing.pipeline.stream;

import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream<E extends Event> {
  Mono<E> pub(E e);

  Flux<E> sub();

  static <E extends Event> EventStream<E> create(String topic,
                                                 int partition,
                                                 ESMsgStream esMsgStream,
                                                 TextTransformer transformer,
                                                 Class<E> eClass) {
    return new EventStreamImpl<>(topic, partition, esMsgStream, transformer, eClass);
  }
}

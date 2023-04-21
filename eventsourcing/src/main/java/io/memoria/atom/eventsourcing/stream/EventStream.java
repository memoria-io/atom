package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.pipeline.PipelineRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream<E extends Event> {
  Mono<E> pub(E e);

  /**
   * @return Infinite subscription
   */
  Flux<E> sub();

  /**
   * @return subscribe until eventId (key) is matched
   */
  default Flux<E> subUntil(Id eventId) {
    return sub().takeUntil(e -> e.eventId().equals(eventId));
  }

  static <E extends Event> EventStream<E> create(PipelineRoute route,
                                                 ESMsgStream esMsgStream,
                                                 TextTransformer transformer,
                                                 Class<E> eClass) {
    return new EventStreamImpl<>(route.eventTopic(), route.eventSubPubPartition(), esMsgStream, transformer, eClass);
  }

  static <E extends Event> EventStream<E> create(String topic,
                                                 int partition,
                                                 ESMsgStream esMsgStream,
                                                 TextTransformer transformer,
                                                 Class<E> eClass) {
    return new EventStreamImpl<>(topic, partition, esMsgStream, transformer, eClass);
  }
}

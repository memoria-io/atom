package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.Topic;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventStream<E extends Event> {
  Try<E> pub(E e);

  Stream<Try<E>> sub(StateId stateId);

  static <E extends Event> EventStream<E> create(Topic topic,
                                                 ESStream esStream,
                                                 TextTransformer transformer,
                                                 Class<E> eClass) {
    return new EventStreamImpl<>(topic, esStream, transformer, eClass);
  }
}

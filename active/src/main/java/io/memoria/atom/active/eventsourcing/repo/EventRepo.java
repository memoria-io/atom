package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.List;
import java.util.stream.Stream;

public interface EventRepo<E extends Event> {
  Stream<Try<E>> getAll(String topic, StateId stateId);

  Try<Integer> append(String topic, int seqId, E event);

  static <E extends Event> EventRepo<E> from(ESRepo esRepo, TextTransformer transformer, Class<E> eClass) {
    return new AbstractEventRepo<>(esRepo, transformer, eClass);
  }

  /**
   * @return In memory EventRepo
   */
  static <E extends Event> EventRepo<E> from(List<String> topicNames) {
    return new MemEventRepo<>(topicNames);
  }

  /**
   * @return In memory event repo
   */
  static <E extends Event> EventRepo<E> from(String... topicNames) {
    return new MemEventRepo<>(topicNames);
  }
}

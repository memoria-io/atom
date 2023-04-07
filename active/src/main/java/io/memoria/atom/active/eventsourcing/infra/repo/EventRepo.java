package io.memoria.atom.active.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventRepo<E extends Event> {
  Stream<Try<E>> getFirst(StateId stateId);

  Stream<Try<E>> getAll(StateId stateId);

  Stream<Try<E>> getAll(StateId stateId, int seqId);

  Try<Integer> append(int seqId, E event);

  static <E extends Event> EventRepo<E> create(String eventTable,
                                               ESRepo esRepo,
                                               TextTransformer transformer,
                                               Class<E> eClass) {
    return new EventRepoImpl<>(eventTable, esRepo, transformer, eClass);
  }
}

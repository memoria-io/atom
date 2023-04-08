package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;

public interface EventRepo<E extends Event> {
  Flux<E> getAll(StateId stateId);

  Flux<E> append(Flux<E> events);

  static <E extends Event> EventRepo<E> create(String eventTable,
                                               ESRowRepo esRowRepo,
                                               TextTransformer transformer,
                                               Class<E> eClass) {
    return new EventRepoImpl<>(eventTable, esRowRepo, transformer, eClass);
  }
}

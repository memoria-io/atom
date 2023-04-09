package io.memoria.atom.eventsourcing.repo;

import io.memoria.atom.core.repo.ESRowRepo;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.StateId;
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

package io.memoria.reactive.eventsourcing.adapter.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.Route;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.reactive.eventsourcing.infra.repo.ESRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepo<E extends Event> {
  Mono<E> getFirst(StateId stateId);

  Flux<E> getAll(StateId stateId);

  Flux<E> getAll(StateId stateId, int seqId);

  Mono<Integer> append(int seqId, E event);

  static <E extends Event> EventRepo<E> create(Route route,
                                               ESRepo esRepo,
                                               TextTransformer transformer,
                                               Class<E> eClass) {
    return new EventRepoImpl<>(route, esRepo, transformer, eClass);
  }
}

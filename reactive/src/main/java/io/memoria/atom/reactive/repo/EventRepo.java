package io.memoria.atom.reactive.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventRepo<E extends Event> {
  Stream<Try<E>> get(StateId stateId);

  Try<E> push(E event);
}

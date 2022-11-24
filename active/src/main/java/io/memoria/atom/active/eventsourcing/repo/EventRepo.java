package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventRepo {
  Stream<EventMsg> getAll(String topic, StateId stateId);

  Try<Integer> append(EventMsg event);
}

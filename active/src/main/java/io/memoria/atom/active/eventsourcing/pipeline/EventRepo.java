package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventRepo<E extends Event> {
  Stream<Try<E>> getAll(String topic, StateId stateId);

  Try<Integer> append(String topic, int seqId, E event);
}

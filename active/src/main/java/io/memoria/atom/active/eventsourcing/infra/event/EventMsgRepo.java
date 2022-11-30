package io.memoria.atom.active.eventsourcing.infra.event;

import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface EventMsgRepo {
  Stream<EventMsg> getAll(String topic, StateId stateId);

  Try<Integer> append(EventMsg event);
}

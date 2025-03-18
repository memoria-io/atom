package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.state.StateId;

public interface AggregateFactory {
  Aggregate create(StateId stateId);
}

package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.state.StateId;

public interface ActorFactory {
  StateAggregate create(StateId stateId);
}

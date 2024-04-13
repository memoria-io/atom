package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.state.StateId;

public interface ActorFactory {
  StateActor create(StateId stateId);
}

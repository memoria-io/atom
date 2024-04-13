package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.state.StateId;

public abstract class AbstractStateActor implements StateActor {
  private final StateId stateId;

  protected AbstractStateActor(StateId stateId) {
    this.stateId = stateId;
  }

  @Override
  public StateId stateId() {
    return this.stateId;
  }
}

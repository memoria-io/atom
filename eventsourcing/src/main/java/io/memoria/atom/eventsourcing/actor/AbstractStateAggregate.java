package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.state.StateId;

public abstract class AbstractStateAggregate implements StateAggregate {
  private final StateId stateId;

  protected AbstractStateAggregate(StateId stateId) {
    this.stateId = stateId;
  }

  @Override
  public StateId stateId() {
    return this.stateId;
  }
}

package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.state.StateId;

public abstract class AbstractAggregate implements Aggregate {
  private final StateId stateId;

  protected AbstractAggregate(StateId stateId) {
    this.stateId = stateId;
  }

  @Override
  public StateId stateId() {
    return this.stateId;
  }
}

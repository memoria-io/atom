package io.memoria.atom.eventsourcing.handler;

import io.memoria.atom.eventsourcing.state.StateId;

public abstract class AbstractCommandHandler implements CommandHandler {
  private final StateId stateId;

  protected AbstractCommandHandler(StateId stateId) {
    this.stateId = stateId;
  }

  @Override
  public StateId stateId() {
    return this.stateId;
  }
}

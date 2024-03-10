package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.state.State;

public class StateException extends ESException {
  private final State state;

  protected StateException(String msg, State state) {
    super(msg);
    this.state = state;
  }

  public State getState() {
    return state;
  }
}

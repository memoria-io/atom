package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.ESRuntimeException;
import io.memoria.atom.eventsourcing.state.State;

public class StateRuntimeException extends ESRuntimeException {
  private final State state;

  protected StateRuntimeException(String msg, State state) {
    super(msg);
    this.state = state;
  }

  public State getState() {
    return state;
  }
}

package com.marmoush.atom.eventsourcing.state.exceptions;

import com.marmoush.atom.eventsourcing.state.State;

public class StateRuntimeException extends RuntimeException {
  private final State state;

  protected StateRuntimeException(String msg, State state) {
    super(msg);
    this.state = state;
  }

  public State getState() {
    return state;
  }
}

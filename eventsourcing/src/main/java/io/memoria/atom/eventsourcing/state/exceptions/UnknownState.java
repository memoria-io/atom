package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.state.State;

public class UnknownState extends RuntimeException implements StateException {
  private static final String MESSAGE = "Unknown State: %s[%s] implementation";
  private final State state;

  protected UnknownState(State state) {
    super(MESSAGE.formatted(state.getClass().getSimpleName(), state.meta()));
    this.state = state;
  }

  @Override
  public String message() {
    return super.getMessage();
  }

  @Override
  public State state() {
    return state;
  }

  public static UnknownState of(State state) {
    return new UnknownState(state);
  }
}

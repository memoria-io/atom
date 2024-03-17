package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.state.State;

@SuppressWarnings("java:S110")
public class UnknownState extends StateRuntimeException {
  private static final String MESSAGE = "Unknown State: %s[%s] implementation";

  protected UnknownState(State state) {
    super(MESSAGE.formatted(state.getClass().getSimpleName(), state.meta()), state);
  }

  public static UnknownState of(State state) {
    return new UnknownState(state);
  }
}

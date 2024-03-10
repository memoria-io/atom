package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.state.State;

public class UnknownState extends StateException {
  private static final String message = "Unknown State: %s[%s] implementation";

  protected UnknownState(State state) {
    super(message.formatted(state.getClass().getSimpleName(), state.meta()), state);
  }

  public static UnknownState of(State state) {
    return new UnknownState(state);
  }
}

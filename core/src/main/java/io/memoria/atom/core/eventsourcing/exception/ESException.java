package io.memoria.atom.core.eventsourcing.exception;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;

public interface ESException {
  class InvalidCommand extends IllegalArgumentException implements ESException {
    private InvalidCommand(String stateName, String commandName) {
      super("Invalid command (%s) for the state (%s)".formatted(commandName, stateName));
    }

    public static InvalidCommand create(State state, Command command) {
      return new InvalidCommand(state.getClass().getSimpleName(), command.getClass().getSimpleName());
    }
  }

  class InvalidEvent extends IllegalArgumentException implements ESException {
    private static final String msg = "Invalid evolution of: %s on current state: %s, this should never happen";

    private InvalidEvent(State state, Event event) {
      super(msg.formatted(state.getClass().getSimpleName(), event.getClass().getSimpleName()));
    }

    public static InvalidEvent of(State state, Event event) {
      return new InvalidEvent(state, event);
    }
  }
}

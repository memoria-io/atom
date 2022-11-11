package io.memoria.atom.eventsourcing.exception;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.State;

public interface DeciderException {

  class InvalidCommand extends RuntimeException implements DeciderException {
    private InvalidCommand(String stateName, String commandName) {
      super("Invalid command (%s) for the state (%s)".formatted(commandName, stateName));
    }

    public static InvalidCommand create(State state, Command command) {
      return new InvalidCommand(state.getClass().getSimpleName(), command.getClass().getSimpleName());
    }
  }
}

package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.State;

public class InvalidCommand extends CommandException {
  protected InvalidCommand(String msg, Command command) {
    super(msg, command);
  }

  public static InvalidCommand ofCreation(Command command) {
    var msg = "Invalid creation command: %s[%s]".formatted(command.getClass().getSimpleName(), command.meta());
    return new InvalidCommand(msg, command);
  }

  public static InvalidCommand ofEvolution(State state, Command command) {
    var msg = "Invalid evolution command: %s[%s] to the state: %s[%s]".formatted(command.getClass().getSimpleName(),
                                                                                 command.meta(),
                                                                                 state.getClass().getSimpleName(),
                                                                                 state.meta());
    return new InvalidCommand(msg, command);
  }
}

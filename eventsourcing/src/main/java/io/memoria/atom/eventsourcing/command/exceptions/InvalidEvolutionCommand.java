package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.State;

public class InvalidEvolutionCommand extends CommandException {
  protected InvalidEvolutionCommand(String msg, Command command) {
    super(msg, command);
  }

  public static InvalidEvolutionCommand of(State state, Command command) {
    var msg = "Invalid evolution command: %s[%s] to the state: %s[%s]".formatted(command.getClass().getSimpleName(),
                                                                                 command.meta(),
                                                                                 state.getClass().getSimpleName(),
                                                                                 state.meta());
    return new InvalidEvolutionCommand(msg, command);
  }
}

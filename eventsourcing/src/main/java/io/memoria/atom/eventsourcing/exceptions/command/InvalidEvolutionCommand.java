package io.memoria.atom.eventsourcing.exceptions.command;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.exceptions.ESException;

public class InvalidEvolutionCommand extends ESException {
  protected InvalidEvolutionCommand(String msg) {
    super(msg);
  }

  public static InvalidEvolutionCommand of(Command command, State state) {
    var msg = "Invalid evolution command: %s[%s] to the state: %s[%s]".formatted(command.getClass().getSimpleName(),
                                                                                 command.meta(),
                                                                                 state.getClass().getSimpleName(),
                                                                                 state.meta());
    return new InvalidEvolutionCommand(msg);
  }
}

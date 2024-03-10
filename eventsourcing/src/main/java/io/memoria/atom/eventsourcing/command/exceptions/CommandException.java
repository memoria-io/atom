package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;

public class CommandException extends ESException {
  private final Command command;

  protected CommandException(String msg, Command command) {
    super(msg);
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }
}

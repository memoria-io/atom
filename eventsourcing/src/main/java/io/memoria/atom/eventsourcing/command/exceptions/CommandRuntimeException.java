package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.ESRuntimeException;
import io.memoria.atom.eventsourcing.command.Command;

public class CommandRuntimeException extends ESRuntimeException {
  private final Command command;

  protected CommandRuntimeException(String msg, Command command) {
    super(msg);
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }
}

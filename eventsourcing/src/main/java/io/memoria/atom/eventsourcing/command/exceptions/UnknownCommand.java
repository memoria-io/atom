package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;

public class UnknownCommand extends CommandException {
  private static final String message = "Unknown Command: %s[%s] implementation";

  protected UnknownCommand(Command command) {
    super(message.formatted(command.getClass().getSimpleName(), command.meta()), command);
  }

  public static UnknownCommand of(Command command) {
    return new UnknownCommand(command);
  }
}

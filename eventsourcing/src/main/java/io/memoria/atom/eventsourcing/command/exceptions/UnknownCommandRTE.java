package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;

public class UnknownCommandRTE extends CommandRTE {
  private static final String MESSAGE = "Unknown Command: %s[%s] implementation";

  protected UnknownCommandRTE(Command command) {
    this(MESSAGE.formatted(command.getClass().getSimpleName(), command.meta()), command);
  }

  protected UnknownCommandRTE(String message, Command command) {
    super(message, command);
  }

  public static UnknownCommandRTE of(Command command) {
    return new UnknownCommandRTE(command);
  }
}

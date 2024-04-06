package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;

public class UnknownCommand extends RuntimeException implements CommandException {
  private static final String MESSAGE = "Unknown Command: %s[%s] implementation";
  private final Command command;

  protected UnknownCommand(Command command) {
    super(MESSAGE.formatted(command.getClass().getSimpleName(), command.meta()));
    this.command = command;
  }

  @Override
  public String message() {
    return super.getMessage();
  }

  @Override
  public Command command() {
    return command;
  }

  public static UnknownCommand of(Command command) {
    return new UnknownCommand(command);
  }
}

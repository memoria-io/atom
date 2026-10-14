package com.marmoush.atom.eventsourcing.command.exceptions;

import com.marmoush.atom.eventsourcing.command.Command;

public class CommandException extends Exception {
  private final Command command;

  protected CommandException(String msg, Command command) {
    super(msg);
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }
}

package com.marmoush.atom.eventsourcing.command.exceptions;

import com.marmoush.atom.eventsourcing.command.Command;

public class CommandRTE extends RuntimeException {
  private final Command command;

  protected CommandRTE(String msg, Command command) {
    super(msg);
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }
}

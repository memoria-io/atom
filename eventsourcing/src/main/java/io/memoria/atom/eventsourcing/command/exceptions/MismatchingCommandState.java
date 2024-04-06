package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.StateId;

public class MismatchingCommandState extends RuntimeException implements CommandException {
  private final Command command;

  protected MismatchingCommandState(String msg, Command command) {
    super(msg);
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

  public static MismatchingCommandState of(Command command) {
    var msg = "The command's stateId:%s doesn't belong here".formatted(command.shardKey());
    return new MismatchingCommandState(msg, command);
  }

  public static MismatchingCommandState of(StateId stateId, Command command) {
    var msg = "The command's stateId:%s doesn't match expected stateId:%s".formatted(command.shardKey(), stateId);
    return new MismatchingCommandState(msg, command);
  }
}

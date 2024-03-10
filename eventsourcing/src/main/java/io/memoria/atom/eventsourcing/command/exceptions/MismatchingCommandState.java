package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.State;

public class MismatchingCommandState extends CommandException {
  protected MismatchingCommandState(String msg, Command command) {
    super(msg, command);
  }

  public static MismatchingCommandState of(Command command) {
    var msg = "The command's stateId:%s doesn't belong here".formatted(command.shardKey());
    return new MismatchingCommandState(msg, command);
  }

  public static MismatchingCommandState of(Command command, State state) {
    var msg = "The Command's stateId:%s doesn't match stateId:%s".formatted(command.shardKey(), state.shardKey());
    return new MismatchingCommandState(msg, command);
  }
}

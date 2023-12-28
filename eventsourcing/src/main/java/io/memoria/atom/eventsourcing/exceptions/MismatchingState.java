package io.memoria.atom.eventsourcing.exceptions;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.State;

public class MismatchingState extends IllegalArgumentException implements ESException {
  protected MismatchingState(String msg) {
    super(msg);
  }

  public static MismatchingState shardKey(Command command, State state) {
    var msg = "The Command's stateId:%s doesn't match stateId:%s".formatted(command.shardKey(), state.shardKey());
    return new MismatchingState(msg);
  }

  public static MismatchingState shardKey(Command command) {
    var msg = "The command's stateId:%s doesn't belong here".formatted(command.shardKey());
    return new MismatchingState(msg);
  }
}

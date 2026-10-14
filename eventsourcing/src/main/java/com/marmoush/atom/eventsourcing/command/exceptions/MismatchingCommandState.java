package com.marmoush.atom.eventsourcing.command.exceptions;

import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.state.StateId;

public class MismatchingCommandState extends CommandRTE {
  protected MismatchingCommandState(String msg, Command command) {
    super(msg, command);
  }

  public static MismatchingCommandState of(StateId stateId, Command command) {
    var msg = "The command's stateId:%s doesn't match expected stateId:%s".formatted(command.pKey(), stateId);
    return new MismatchingCommandState(msg, command);
  }
}

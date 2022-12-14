package io.memoria.atom.active.eventsourcing.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record CloseAccount(CommandId commandId, StateId stateId) implements UserCommand {
  public static CloseAccount of(StateId stateId) {
    return new CloseAccount(CommandId.randomUUID(), stateId);
  }
}

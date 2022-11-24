package io.memoria.atom.active.eventsourcing.banking.command;

import io.memoria.atom.active.eventsourcing.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record MarkAsSuccessful(CommandId commandId, StateId stateId, Transfer transfer) implements UserCommand {
  public static MarkAsSuccessful of(Transfer transfer) {
    return new MarkAsSuccessful(CommandId.randomUUID(), transfer.sender(), transfer);
  }
}

package io.memoria.atom.es.active.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.state.Transfer;

public record MarkAsSuccessful(CommandId commandId, StateId stateId, Transfer transfer) implements AccountCommand {
  public static MarkAsSuccessful of(Transfer transfer) {
    return new MarkAsSuccessful(CommandId.randomUUID(), transfer.sender(), transfer);
  }
}
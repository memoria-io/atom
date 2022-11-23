package io.memoria.atom.reactive.banking.command;

import io.memoria.atom.reactive.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record MarkAsRejected(CommandId commandId, StateId stateId, Transfer transfer) implements AccountCommand {
  public static MarkAsRejected of(Transfer transfer) {
    return new MarkAsRejected(CommandId.randomUUID(), transfer.sender(), transfer);
  }
}

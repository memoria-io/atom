package io.memoria.atom.es.active.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.state.Transfer;

public record CreateTransfer(CommandId commandId, StateId stateId, Transfer transfer) implements AccountCommand {
  public static CreateTransfer of(Transfer transfer) {
    return new CreateTransfer(CommandId.randomUUID(), transfer.sender(), transfer);
  }
}

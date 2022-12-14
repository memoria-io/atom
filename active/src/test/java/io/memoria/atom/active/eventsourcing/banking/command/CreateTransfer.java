package io.memoria.atom.active.eventsourcing.banking.command;

import io.memoria.atom.active.eventsourcing.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record CreateTransfer(CommandId commandId, StateId stateId, Transfer transfer) implements UserCommand {
  public static CreateTransfer of(Transfer transfer) {
    return new CreateTransfer(CommandId.randomUUID(), transfer.sender(), transfer);
  }
}

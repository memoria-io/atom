package io.memoria.atom.active.eventsourcing.banking.event;

import io.memoria.atom.active.eventsourcing.banking.command.CreateTransfer;
import io.memoria.atom.active.eventsourcing.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;

public record TransferCreated(EventId eventId, CommandId commandId, StateId stateId, Transfer transfer)
        implements UserEvent {
  public static TransferCreated by(CreateTransfer cmd) {
    return new TransferCreated(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.transfer());
  }
}

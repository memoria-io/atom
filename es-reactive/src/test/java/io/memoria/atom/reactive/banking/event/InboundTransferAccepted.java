package io.memoria.atom.reactive.banking.event;

import io.memoria.atom.reactive.banking.command.HandleInboundTransfer;
import io.memoria.atom.reactive.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;

public record InboundTransferAccepted(EventId eventId, CommandId commandId, StateId stateId, Transfer transfer)
        implements UserEvent {
  public static InboundTransferAccepted by(HandleInboundTransfer cmd) {
    return new InboundTransferAccepted(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.transfer());
  }
}

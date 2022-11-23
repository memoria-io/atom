package io.memoria.atom.reactive.banking.event;

import io.memoria.atom.reactive.banking.command.MarkAsSuccessful;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;

public record OutboundTransferAccepted(EventId eventId, CommandId commandId, StateId stateId, Id transactionId)
        implements UserEvent {
  public static OutboundTransferAccepted by(MarkAsSuccessful cmd) {
    return new OutboundTransferAccepted(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.transfer().id());
  }
}

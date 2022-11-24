package io.memoria.atom.active.banking.event;

import io.memoria.atom.active.banking.command.MarkAsSuccessful;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.id.Id;

public record OutboundTransferAccepted(EventId eventId, CommandId commandId, StateId stateId, Id transactionId)
        implements UserEvent {
  public static OutboundTransferAccepted by(MarkAsSuccessful cmd) {
    return new OutboundTransferAccepted(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.transfer().id());
  }
}

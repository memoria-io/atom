package io.memoria.atom.es.active.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.command.MarkAsRejected;
import io.memoria.atom.es.active.banking.state.Transfer;

public record OutboundTransferRejected(EventId eventId, CommandId commandId, StateId stateId, Transfer transfer)
        implements UserEvent {

  public static OutboundTransferRejected by(MarkAsRejected cmd) {
    return new OutboundTransferRejected(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.transfer());
  }
}
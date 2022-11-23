package io.memoria.atom.es.active.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.command.CloseAccount;

public record AccountClosed(EventId eventId, CommandId commandId, StateId stateId) implements UserEvent {
  public static AccountClosed by(CloseAccount cmd) {
    return new AccountClosed(EventId.randomUUID(), cmd.commandId(), cmd.stateId());
  }
}

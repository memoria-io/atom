package io.memoria.atom.active.banking.event;

import io.memoria.atom.active.banking.command.CloseAccount;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;

public record AccountClosed(EventId eventId, CommandId commandId, StateId stateId) implements UserEvent {
  public static AccountClosed by(CloseAccount cmd) {
    return new AccountClosed(EventId.randomUUID(), cmd.commandId(), cmd.stateId());
  }
}

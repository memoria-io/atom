package io.memoria.atom.active.banking.event;

import io.memoria.atom.active.banking.command.CreateAccount;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;

public record AccountCreated(EventId eventId, CommandId commandId, StateId stateId, String name, int balance)
        implements UserEvent {
  public static AccountCreated by(CreateAccount cmd) {
    return new AccountCreated(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.accountName(), cmd.balance());
  }
}

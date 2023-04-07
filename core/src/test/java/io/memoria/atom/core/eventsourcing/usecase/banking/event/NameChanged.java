package io.memoria.atom.core.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.ChangeName;

public record NameChanged(EventId eventId, CommandId commandId, StateId accountId, String newName)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static NameChanged from(ChangeName cmd) {
    return new NameChanged(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.name());
  }
}

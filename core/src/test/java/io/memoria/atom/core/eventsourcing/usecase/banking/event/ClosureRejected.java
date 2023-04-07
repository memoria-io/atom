package io.memoria.atom.core.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.CloseAccount;

public record ClosureRejected(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static ClosureRejected from(CloseAccount cmd) {
    return new ClosureRejected(EventId.randomUUID(), cmd.commandId(), cmd.stateId());
  }
}

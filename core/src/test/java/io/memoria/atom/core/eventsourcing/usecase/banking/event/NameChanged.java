package io.memoria.atom.core.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.ChangeName;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Account;

public record NameChanged(EventId eventId, int seqId, CommandId commandId, StateId accountId, String newName)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static NameChanged from(Account account, ChangeName cmd) {
    return new NameChanged(EventId.randomUUID(), account.seqId() + 1, cmd.commandId(), cmd.stateId(), cmd.name());
  }
}

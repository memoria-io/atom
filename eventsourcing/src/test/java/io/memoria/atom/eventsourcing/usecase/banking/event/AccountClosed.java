package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.CloseAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record AccountClosed(EventId eventId, int seqId, CommandId commandId, StateId accountId)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountClosed from(Account account, CloseAccount cmd) {
    return new AccountClosed(EventId.randomUUID(), account.seqId() + 1, cmd.commandId(), cmd.stateId());
  }
}

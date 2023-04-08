package io.memoria.atom.core.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.CreateAccount;

public record AccountCreated(EventId eventId,
                             int seqId,
                             CommandId commandId,
                             StateId accountId,
                             String name,
                             int balance) implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountCreated from(CreateAccount cmd) {
    return new AccountCreated(EventId.randomUUID(),
                              0,
                              cmd.commandId(),
                              cmd.accountId(),
                              cmd.accountname(),
                              cmd.balance());
  }
}

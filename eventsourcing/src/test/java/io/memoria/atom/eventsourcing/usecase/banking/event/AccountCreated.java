package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.CreateAccount;

public record AccountCreated(Id eventId, int seqId, Id commandId, Id accountId, String name, int balance)
        implements AccountEvent {
  @Override
  public Id stateId() {
    return accountId;
  }

  public static AccountCreated from(CreateAccount cmd) {
    return new AccountCreated(Id.of(), 0, cmd.commandId(), cmd.accountId(), cmd.accountname(), cmd.balance());
  }
}

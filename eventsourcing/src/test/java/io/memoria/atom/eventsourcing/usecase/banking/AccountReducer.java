package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.rule.Reducer;
import io.memoria.atom.eventsourcing.usecase.banking.event.AccountClosed;
import io.memoria.atom.eventsourcing.usecase.banking.event.AccountCreated;
import io.memoria.atom.eventsourcing.usecase.banking.event.AccountEvent;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.eventsourcing.usecase.banking.state.ClosedAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;

public record AccountReducer() implements Reducer<Account, AccountEvent> {
  @Override
  public AccountEvent apply(Account account) {
    return switch (account) {
      case OpenAccount openAccount -> accountCreated(openAccount);
      case ClosedAccount acc ->
              new AccountClosed(EventId.randomUUID(), acc.seqId(), CommandId.randomUUID(), acc.stateId());
    };
  }

  private AccountCreated accountCreated(OpenAccount openAccount) {
    return new AccountCreated(EventId.randomUUID(),
                              0,
                              CommandId.randomUUID(),
                              openAccount.stateId(),
                              openAccount.name(),
                              openAccount.balance());
  }
}

package io.memoria.atom.core.eventsourcing.usecase.banking;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.rule.Reducer;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.AccountClosed;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.AccountCreated;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.AccountEvent;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Acc;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.ClosedAccount;


public record AccountReducer() implements Reducer<Account, AccountEvent> {
  @Override
  public AccountEvent apply(Account account) {
    return switch (account) {
      case Acc acc -> accountCreated(acc);
      case ClosedAccount acc -> new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId());
    };
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name(), acc.balance());
  }
}

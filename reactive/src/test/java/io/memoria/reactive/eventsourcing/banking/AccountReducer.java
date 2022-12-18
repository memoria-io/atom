package io.memoria.reactive.eventsourcing.banking;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.rule.Reducer;
import io.memoria.reactive.eventsourcing.banking.event.AccountClosed;
import io.memoria.reactive.eventsourcing.banking.event.AccountCreated;
import io.memoria.reactive.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.eventsourcing.banking.state.*;

public record AccountReducer() implements Reducer<Account, AccountEvent> {
  @Override
  public AccountEvent apply(Account account) {
    return switch (account) {
      case Visitor acc -> throw new IllegalArgumentException("State is an initial state");
      case Acc acc -> accountCreated(acc);
      case ClosedAccount acc -> new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId());
    };
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name(), acc.balance());
  }
}

package io.memoria.reactive.eventsourcing.banking;

import io.memoria.atom.core.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.core.eventsourcing.rule.Evolver;
import io.memoria.reactive.eventsourcing.banking.event.*;
import io.memoria.reactive.eventsourcing.banking.state.Acc;
import io.memoria.reactive.eventsourcing.banking.state.Account;
import io.memoria.reactive.eventsourcing.banking.state.ClosedAccount;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public record AccountEvolver() implements Evolver<Account, AccountEvent> {
  @Override
  public Account apply(Account account, AccountEvent accountEvent) {
    return switch (account) {
      case Acc acc -> handle(acc, accountEvent);
      case ClosedAccount acc -> acc;
    };
  }

  @Override
  public Account apply(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e -> new Acc(e.stateId(), e.name(), e.balance(), 0);
      default -> throw InvalidEvent.of(accountEvent);
    };
  }

  private Account handle(Acc acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> acc.withCredit(e.amount());
      case Debited e -> acc.withDebit(e.amount());
      case DebitConfirmed e -> acc.withDebitConfirmed();
      case AccountClosed e -> new ClosedAccount(e.accountId());
      default -> acc;
    };
  }
}

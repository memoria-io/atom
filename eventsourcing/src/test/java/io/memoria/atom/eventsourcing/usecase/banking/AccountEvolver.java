package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.eventsourcing.usecase.banking.state.ClosedAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public record AccountEvolver() implements Evolver<Account, AccountEvent> {
  @Override
  public Account apply(Account account, AccountEvent accountEvent) {
    return switch (account) {
      case OpenAccount openAccount -> handle(openAccount, accountEvent);
      case ClosedAccount acc -> acc;
    };
  }

  @Override
  public Account apply(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e -> new OpenAccount(e.stateId(), e.name(), e.balance(), 0);
      default -> throw InvalidEvent.of(accountEvent);
    };
  }

  private Account handle(OpenAccount openAccount, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> openAccount.withCredit(e.amount());
      case NameChanged e -> openAccount.withName(e.newName());
      case Debited e -> openAccount.withDebit(e.amount());
      case DebitConfirmed e -> openAccount.withDebitConfirmed();
      case AccountClosed e -> new ClosedAccount(e.accountId());
      default -> openAccount;
    };
  }
}

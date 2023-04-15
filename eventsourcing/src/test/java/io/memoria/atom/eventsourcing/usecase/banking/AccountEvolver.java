package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.eventsourcing.usecase.banking.state.ClosedAccount;

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
      case AccountCreated e -> new OpenAccount(e.stateId(), e.seqId(), e.name(), e.balance(), 0);
      default -> throw InvalidEvent.of(accountEvent);
    };
  }

  private Account handle(OpenAccount openAccount, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> openAccount.withCredit(e.seqId(), e.amount());
      case Debited e -> openAccount.withDebit(e.seqId(), e.amount());
      case DebitConfirmed e -> openAccount.withDebitConfirmed(e.seqId());
      case AccountClosed e -> new ClosedAccount(e.accountId(), e.seqId());
      default -> openAccount;
    };
  }
}

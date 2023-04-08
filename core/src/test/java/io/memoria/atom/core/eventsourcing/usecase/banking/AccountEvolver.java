package io.memoria.atom.core.eventsourcing.usecase.banking;

import io.memoria.atom.core.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.core.eventsourcing.rule.Evolver;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Acc;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.ClosedAccount;

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
      case AccountCreated e -> new Acc(e.stateId(), e.seqId(), e.name(), e.balance(), 0);
      default -> throw InvalidEvent.of(accountEvent);
    };
  }

  private Account handle(Acc acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> acc.withCredit(e.seqId(), e.amount());
      case Debited e -> acc.withDebit(e.seqId(), e.amount());
      case DebitConfirmed e -> acc.withDebitConfirmed(e.seqId());
      case AccountClosed e -> new ClosedAccount(e.accountId(), e.seqId());
      default -> acc;
    };
  }
}

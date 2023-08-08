package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException.InvalidEvent;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountClosed;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountCreated;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.banking.event.DebitConfirmed;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.event.NameChanged;
import io.memoria.atom.testsuite.eventsourcing.banking.state.Account;
import io.memoria.atom.testsuite.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;

import java.util.function.Supplier;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public record AccountEvolver(Supplier<Id> idSupplier, Supplier<Long> timeSupplier)
        implements Evolver<Account, AccountEvent> {
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
      case AccountCreated e -> new OpenAccount(e.accountId(), e.name(), e.balance(), 0, 0, 0);
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

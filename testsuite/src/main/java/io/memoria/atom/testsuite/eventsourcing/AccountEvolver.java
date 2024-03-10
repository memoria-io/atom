package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.StateMeta;
import io.memoria.atom.eventsourcing.exceptions.UnknownImplementation;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.testsuite.eventsourcing.event.AccountClosed;
import io.memoria.atom.testsuite.eventsourcing.event.AccountCreated;
import io.memoria.atom.testsuite.eventsourcing.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.event.DebitConfirmed;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.event.NameChanged;
import io.memoria.atom.testsuite.eventsourcing.state.Account;
import io.memoria.atom.testsuite.eventsourcing.state.ClosedAccount;
import io.memoria.atom.testsuite.eventsourcing.state.OpenAccount;

import static io.memoria.atom.eventsourcing.Validations.instanceOf;

public record AccountEvolver() implements Evolver {

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  @Override
  public Account apply(Event event) {
    return switch (event) {
      case AccountCreated e -> {
        StateMeta meta = new StateMeta(e.accountId());
        yield new OpenAccount(meta, e.name(), e.balance(), 0, 0, 0);
      }
      default -> throw new RuntimeException(UnknownImplementation.of(event));
    };
  }

  @Override
  public Account apply(State state, Event event) {
    return instanceOf(state, Account.class, event, AccountEvent.class).map(tup -> switch (tup._1) {
      case OpenAccount openAccount -> handle(openAccount, tup._2);
      case ClosedAccount acc -> acc;
    }).get();
  }

  private Account handle(OpenAccount account, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> account.withCredit(e.amount());
      case NameChanged e -> account.withName(e.newName());
      case Debited e -> account.withDebit(e.amount());
      case DebitConfirmed _ -> account.withDebitConfirmed();
      case AccountClosed _ -> new ClosedAccount(stateMeta(account));
      default -> account;
    };
  }
}

package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
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

public record AccountEvolver() implements Evolver {

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  @Override
  public Account apply(Event event) {
    return switch (event) {
      case AccountCreated e -> {
        StateMeta meta = new StateMeta(e.accountId());
        yield new OpenAccount(meta, e.name(), e.balance(), 0, 0, 0);
      }
      default -> throw new RuntimeException(UnknownEvent.of(event));
    };
  }

  @Override
  public Account apply(State state, Event event) {
    if (state instanceof Account account) {
      if (event instanceof AccountEvent accountEvent) {
        return switch (account) {
          case OpenAccount openAccount -> handle(openAccount, accountEvent);
          case ClosedAccount acc -> acc;
        };
      } else {
        throw new RuntimeException(UnknownEvent.of(event));
      }
    } else {
      throw new RuntimeException(UnknownState.of(state));
    }
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

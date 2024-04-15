package io.memoria.atom.tests.eventsourcing;

import io.memoria.atom.eventsourcing.aggregate.Evolver;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import io.memoria.atom.tests.eventsourcing.event.AccountClosed;
import io.memoria.atom.tests.eventsourcing.event.AccountCreated;
import io.memoria.atom.tests.eventsourcing.event.AccountEvent;
import io.memoria.atom.tests.eventsourcing.event.Credited;
import io.memoria.atom.tests.eventsourcing.event.DebitConfirmed;
import io.memoria.atom.tests.eventsourcing.event.Debited;
import io.memoria.atom.tests.eventsourcing.event.NameChanged;
import io.memoria.atom.tests.eventsourcing.state.Account;
import io.memoria.atom.tests.eventsourcing.state.ClosedAccount;
import io.memoria.atom.tests.eventsourcing.state.OpenAccount;

public record AccountEvolver() implements Evolver {

  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public State evolve(Event event, StateMeta stateMeta) {
    return switch (event) {
      case AccountCreated e -> new OpenAccount(stateMeta, e.name(), e.balance(), 0, 0, 0);
      default -> throw UnknownEvent.of(event);
    };
  }

  @Override
  public State evolve(State state, Event event, StateMeta stateMeta) {
    if (state instanceof Account account) {
      if (event instanceof AccountEvent accountEvent) {
        return switch (account) {
          case OpenAccount openAccount -> handle(openAccount, accountEvent, stateMeta);
          case ClosedAccount acc -> acc;
        };
      } else {
        throw UnknownEvent.of(event);
      }
    } else {
      throw UnknownState.of(state);
    }
  }

  private Account handle(OpenAccount account, AccountEvent accountEvent, StateMeta stateMeta) {
    return switch (accountEvent) {
      case Credited e -> account.withCredit(e.amount());
      case NameChanged e -> account.withName(e.newName());
      case Debited e -> account.withDebit(e.amount());
      case DebitConfirmed _ -> account.withDebitConfirmed();
      case AccountClosed _ -> new ClosedAccount(stateMeta);
      default -> account;
    };
  }
}

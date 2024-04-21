package eventsourcing.domain;

import io.memoria.atom.eventsourcing.aggregate.Evolver;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import eventsourcing.domain.event.AccountClosed;
import eventsourcing.domain.event.AccountCreated;
import eventsourcing.domain.event.AccountEvent;
import eventsourcing.domain.event.Credited;
import eventsourcing.domain.event.DebitConfirmed;
import eventsourcing.domain.event.Debited;
import eventsourcing.domain.event.NameChanged;
import eventsourcing.domain.state.Account;
import eventsourcing.domain.state.ClosedAccount;
import eventsourcing.domain.state.OpenAccount;

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

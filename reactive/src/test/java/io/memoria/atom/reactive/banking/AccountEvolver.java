package io.memoria.atom.reactive.banking;

import io.memoria.atom.reactive.banking.event.AccountClosed;
import io.memoria.atom.reactive.banking.event.AccountCreated;
import io.memoria.atom.reactive.banking.event.InboundTransferAccepted;
import io.memoria.atom.reactive.banking.event.InboundTransferRejected;
import io.memoria.atom.reactive.banking.event.OutboundTransferAccepted;
import io.memoria.atom.reactive.banking.event.OutboundTransferRejected;
import io.memoria.atom.reactive.banking.event.TransferCreated;
import io.memoria.atom.reactive.banking.event.UserEvent;
import io.memoria.atom.reactive.banking.state.User;
import io.memoria.atom.reactive.banking.state.Visitor;
import io.memoria.atom.reactive.banking.state.ActiveAccount;
import io.memoria.atom.reactive.banking.state.ClosedAccount;
import io.memoria.atom.core.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.core.eventsourcing.rule.Evolver;

public record AccountEvolver() implements Evolver<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent accountEvent) {
    return switch (user) {
      case Visitor acc -> handle(acc, accountEvent);
      case ActiveAccount activeAccount -> handle(activeAccount, accountEvent);
      case ClosedAccount acc -> acc;
    };
  }

  private User handle(Visitor acc, UserEvent accountEvent) {
    if (accountEvent instanceof AccountCreated e) {
      return new ActiveAccount(e.stateId(), e.name(), e.balance(), 0);
    } else {
      throw InvalidEvent.of(acc, accountEvent);
    }
  }

  private User handle(ActiveAccount activeAccount, UserEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e -> throw InvalidEvent.of(activeAccount, e);
      case TransferCreated e -> activeAccount.with(e);
      case InboundTransferAccepted e -> activeAccount.with(e);
      case InboundTransferRejected e -> activeAccount.with(e);
      case OutboundTransferRejected e -> activeAccount.with(e);
      case OutboundTransferAccepted e -> activeAccount.with(e);
      case AccountClosed e -> new ClosedAccount(e.stateId());
    };
  }
}

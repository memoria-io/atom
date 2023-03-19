package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.event.*;
import io.memoria.atom.active.eventsourcing.banking.state.ActiveAccount;
import io.memoria.atom.active.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.active.eventsourcing.banking.state.User;
import io.memoria.atom.core.eventsourcing.exception.ESException.InvalidEvent;
import io.memoria.atom.core.eventsourcing.rule.Evolver;

public record AccountEvolver() implements Evolver<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent accountEvent) {
    return switch (user) {
      case ActiveAccount activeAccount -> handle(activeAccount, accountEvent);
      case ClosedAccount acc -> acc;
    };
  }

  @Override
  public User apply(UserEvent userEvent) {
    if (userEvent instanceof AccountCreated e) {
      return new ActiveAccount(e.stateId(), e.name(), e.balance(), 0);
    } else {
      throw InvalidEvent.of(userEvent);
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

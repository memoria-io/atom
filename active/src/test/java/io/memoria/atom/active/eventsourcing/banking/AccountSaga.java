package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.command.*;
import io.memoria.atom.active.eventsourcing.banking.event.*;
import io.memoria.atom.core.eventsourcing.rule.Saga;
import io.vavr.control.Option;

public record AccountSaga() implements Saga<UserEvent, UserCommand> {

  @Override
  public Option<UserCommand> apply(UserEvent event) {
    return switch (event) {
      case TransferCreated e -> Option.some(HandleInboundTransfer.of(e.transfer().receiver(), e.transfer()));
      case InboundTransferAccepted e -> Option.some(MarkAsSuccessful.of(e.transfer()));
      case InboundTransferRejected e -> Option.some(MarkAsRejected.of(e.transfer()));
      case default -> Option.none();
    };
  }
}

package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.command.AccountCommand;
import io.memoria.atom.active.eventsourcing.banking.command.HandleInboundTransfer;
import io.memoria.atom.active.eventsourcing.banking.command.MarkAsRejected;
import io.memoria.atom.active.eventsourcing.banking.command.MarkAsSuccessful;
import io.memoria.atom.active.eventsourcing.banking.event.InboundTransferAccepted;
import io.memoria.atom.active.eventsourcing.banking.event.InboundTransferRejected;
import io.memoria.atom.active.eventsourcing.banking.event.TransferCreated;
import io.memoria.atom.active.eventsourcing.banking.event.UserEvent;
import io.memoria.atom.core.eventsourcing.rule.Saga;
import io.vavr.control.Option;

public record AccountSaga() implements Saga<UserEvent, AccountCommand> {

  @Override
  public Option<AccountCommand> apply(UserEvent event) {
    return switch (event) {
      case TransferCreated e -> Option.some(HandleInboundTransfer.of(e.transfer().receiver(), e.transfer()));
      case InboundTransferAccepted e -> Option.some(MarkAsSuccessful.of(e.transfer()));
      case InboundTransferRejected e -> Option.some(MarkAsRejected.of(e.transfer()));
      case default -> Option.none();
    };
  }
}

package io.memoria.atom.es.active.banking;

import io.memoria.atom.core.eventsourcing.rule.Saga;
import io.memoria.atom.es.active.banking.command.AccountCommand;
import io.memoria.atom.es.active.banking.command.HandleInboundTransfer;
import io.memoria.atom.es.active.banking.command.MarkAsRejected;
import io.memoria.atom.es.active.banking.command.MarkAsSuccessful;
import io.memoria.atom.es.active.banking.event.InboundTransferAccepted;
import io.memoria.atom.es.active.banking.event.InboundTransferRejected;
import io.memoria.atom.es.active.banking.event.TransferCreated;
import io.memoria.atom.es.active.banking.event.UserEvent;
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

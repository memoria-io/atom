package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.ConfirmDebit;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record DebitConfirmed(Id eventId, Id commandId, Id debitedAcc) implements AccountEvent {
  @Override
  public Id stateId() {
    return debitedAcc;
  }

  public static DebitConfirmed from(Account account, ConfirmDebit cmd) {
    return new DebitConfirmed(Id.of(), cmd.commandId(), cmd.debitedAcc());
  }
}

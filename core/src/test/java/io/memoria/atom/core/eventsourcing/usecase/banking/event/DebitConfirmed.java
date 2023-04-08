package io.memoria.atom.core.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.ConfirmDebit;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Account;

public record DebitConfirmed(EventId eventId, int seqId, CommandId commandId, StateId debitedAcc)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return debitedAcc;
  }

  public static DebitConfirmed from(Account account, ConfirmDebit cmd) {
    return new DebitConfirmed(EventId.randomUUID(), account.seqId() + 1, cmd.commandId(), cmd.debitedAcc());
  }
}

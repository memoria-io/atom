package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.Credit;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record CreditRejected(EventId eventId,
                             int seqId,
                             CommandId commandId,
                             StateId creditedAcc,
                             StateId debitedAcc,
                             int amount) implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static CreditRejected from(Account acc, Credit cmd) {
    return new CreditRejected(EventId.randomUUID(),
                              acc.seqId() + 1,
                              cmd.commandId(),
                              cmd.creditedAcc(),
                              cmd.debitedAcc(),
                              cmd.amount());
  }
}
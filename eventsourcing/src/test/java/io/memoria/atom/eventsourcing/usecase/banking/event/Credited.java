package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.Credit;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record Credited(EventId eventId,
                       int seqId,
                       CommandId commandId,
                       StateId creditedAcc,
                       StateId debitedAcc,
                       int amount) implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static Credited from(Account account, Credit cmd) {
    return new Credited(EventId.randomUUID(),
                        account.seqId() + 1,
                        cmd.commandId(),
                        cmd.creditedAcc(),
                        cmd.debitedAcc(),
                        cmd.amount());
  }
}
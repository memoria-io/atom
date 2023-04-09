package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.Debit;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record Debited(EventId eventId,
                      int seqId,
                      CommandId commandId,
                      StateId debitedAcc,
                      StateId creditedAcc,
                      int amount) implements AccountEvent {
  @Override
  public StateId stateId() {
    return debitedAcc;
  }

  public static Debited from(Account account, Debit cmd) {
    return new Debited(EventId.randomUUID(),
                       account.seqId() + 1,
                       cmd.commandId(),
                       cmd.debitedAcc(),
                       cmd.creditedAcc(),
                       cmd.amount());
  }
}

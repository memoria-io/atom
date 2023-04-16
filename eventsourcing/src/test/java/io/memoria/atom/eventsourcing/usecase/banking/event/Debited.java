package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.Debit;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record Debited(Id eventId, int seqId, Id commandId, Id debitedAcc, Id creditedAcc, int amount)
        implements AccountEvent {
  @Override
  public Id stateId() {
    return debitedAcc;
  }

  public static Debited from(Account account, Debit cmd) {
    return new Debited(Id.of(),
                       account.seqId() + 1,
                       cmd.commandId(),
                       cmd.debitedAcc(),
                       cmd.creditedAcc(),
                       cmd.amount());
  }
}

package io.memoria.atom.core.eventsourcing.usecase.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record Debit(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount)
        implements AccountCommand {
  @Override
  public StateId accountId() {
    return debitedAcc;
  }

  public static Debit of(StateId debitedAcc, StateId creditedAcc, int amount) {
    return new Debit(CommandId.randomUUID(), debitedAcc, creditedAcc, amount);
  }
}

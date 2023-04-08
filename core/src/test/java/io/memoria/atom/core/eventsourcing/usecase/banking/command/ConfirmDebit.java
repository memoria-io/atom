package io.memoria.atom.core.eventsourcing.usecase.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record ConfirmDebit(CommandId commandId, StateId debitedAcc) implements AccountCommand {
  @Override
  public StateId accountId() {
    return debitedAcc;
  }

  public static ConfirmDebit of(StateId debitedAcc) {
    return new ConfirmDebit(CommandId.randomUUID(), debitedAcc);
  }
}

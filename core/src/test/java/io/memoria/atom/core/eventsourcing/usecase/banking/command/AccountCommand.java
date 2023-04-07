package io.memoria.atom.core.eventsourcing.usecase.banking.command;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }
}

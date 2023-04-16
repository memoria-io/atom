package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.Command;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  Id accountId();

  default Id stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }
}

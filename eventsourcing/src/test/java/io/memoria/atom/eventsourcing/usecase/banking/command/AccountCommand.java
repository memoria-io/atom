package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  default StateId accountId() {
    return meta().stateId();
  }
}

package io.memoria.atom.tests.eventsourcing.command;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.state.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  default StateId accountId() {
    return meta().stateId();
  }
}

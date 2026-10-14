package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.state.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  default StateId accountId() {
    return meta().stateId();
  }
}

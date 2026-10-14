package com.marmoush.javakit.eventsourcing.usecase.banking.command;

import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.state.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  default StateId accountId() {
    return meta().stateId();
  }
}

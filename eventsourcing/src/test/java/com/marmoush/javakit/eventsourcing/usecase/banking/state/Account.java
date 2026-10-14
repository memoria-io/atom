package com.marmoush.javakit.eventsourcing.usecase.banking.state;

import com.marmoush.javakit.eventsourcing.state.State;
import com.marmoush.javakit.eventsourcing.state.StateId;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {
  default StateId accountId() {
    return meta().stateId();
  }
}

package com.marmoush.atom.eventsourcing.usecase.banking.state;

import com.marmoush.atom.eventsourcing.state.State;
import com.marmoush.atom.eventsourcing.state.StateId;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {
  default StateId accountId() {
    return meta().stateId();
  }
}

package io.memoria.atom.testsuite.eventsourcing.banking.state;

import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.StateId;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }
}

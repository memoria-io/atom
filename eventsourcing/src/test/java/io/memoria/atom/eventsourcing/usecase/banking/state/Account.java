package io.memoria.atom.eventsourcing.usecase.banking.state;

import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.StateId;

public sealed interface Account extends State permits Acc, ClosedAccount {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }
}

package io.memoria.atom.core.eventsourcing.usecase.banking.state;

import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.StateId;

public sealed interface Account extends State permits Acc, ClosedAccount {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }
}

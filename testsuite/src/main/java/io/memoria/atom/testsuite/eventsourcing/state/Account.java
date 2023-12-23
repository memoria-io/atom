package io.memoria.atom.testsuite.eventsourcing.state;

import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.StateId;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {
  default StateId accountId(){
    return meta().stateId();
  }
}

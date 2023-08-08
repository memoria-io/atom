package io.memoria.atom.testsuite.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.StateId;

public sealed interface AccountEvent extends Event permits AccountClosed,
                                                           AccountCreated,
                                                           ClosureRejected,
                                                           CreditRejected,
                                                           Credited,
                                                           DebitConfirmed,
                                                           Debited,
                                                           NameChanged {
  default StateId accountId() {
    return meta().stateId();
  }
}

package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.state.StateId;

public sealed interface AccountEvent extends Event permits AccountClosed,
                                                           AccountCreated,
                                                           ClosureRejected,
                                                           CreditRejected,
                                                           Credited,
                                                           DebitConfirmed,
                                                           DebitRejected,
                                                           Debited,
                                                           NameChanged {
  default StateId accountId() {
    return meta().stateId();
  }
}

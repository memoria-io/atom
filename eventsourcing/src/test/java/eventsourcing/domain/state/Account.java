package eventsourcing.domain.state;

import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {
  default StateId accountId() {
    return meta().stateId();
  }
}

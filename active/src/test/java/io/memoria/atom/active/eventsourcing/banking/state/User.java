package io.memoria.atom.active.eventsourcing.banking.state;

import io.memoria.atom.core.eventsourcing.State;

public sealed interface User extends State permits ActiveAccount, ClosedAccount, Visitor {

}

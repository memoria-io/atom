package io.memoria.atom.active.banking.state;

import io.memoria.atom.core.eventsourcing.State;

public sealed interface User extends State permits ActiveAccount, ClosedAccount, Visitor {

}

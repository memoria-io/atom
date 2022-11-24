package io.memoria.atom.active.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record ClosedAccount(StateId stateId) implements User {}

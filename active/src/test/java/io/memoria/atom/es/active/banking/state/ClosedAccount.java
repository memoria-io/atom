package io.memoria.atom.es.active.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record ClosedAccount(StateId stateId) implements User {}

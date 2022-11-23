package io.memoria.reactive.eventsourcing.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record ClosedAccount(StateId accountId) implements Account {}

package io.memoria.atom.core.eventsourcing.usecase.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record ClosedAccount(StateId accountId) implements Account {}

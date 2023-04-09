package io.memoria.atom.eventsourcing.usecase.banking.state;

import io.memoria.atom.eventsourcing.StateId;

public record ClosedAccount(StateId accountId, int seqId) implements Account {}

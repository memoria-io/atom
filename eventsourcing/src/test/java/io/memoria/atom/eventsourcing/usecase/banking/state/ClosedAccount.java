package io.memoria.atom.eventsourcing.usecase.banking.state;

import io.memoria.atom.core.id.Id;

public record ClosedAccount(Id accountId, int seqId) implements Account {}

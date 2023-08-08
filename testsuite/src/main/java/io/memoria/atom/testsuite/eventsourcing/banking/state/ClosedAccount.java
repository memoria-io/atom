package io.memoria.atom.testsuite.eventsourcing.banking.state;

import io.memoria.atom.eventsourcing.StateId;

public record ClosedAccount(StateId accountId) implements Account {}

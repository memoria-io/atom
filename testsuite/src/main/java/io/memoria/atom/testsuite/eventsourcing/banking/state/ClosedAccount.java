package io.memoria.atom.testsuite.eventsourcing.banking.state;

import io.memoria.atom.eventsourcing.StateMeta;

public record ClosedAccount(StateMeta meta) implements Account {}

package io.memoria.atom.testsuite.eventsourcing.state;

import io.memoria.atom.eventsourcing.state.StateMeta;

public record ClosedAccount(StateMeta meta) implements Account {}

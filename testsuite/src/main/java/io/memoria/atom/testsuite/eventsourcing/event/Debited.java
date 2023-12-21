package io.memoria.atom.testsuite.eventsourcing.event;

import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.StateId;

public record Debited(EventMeta meta, StateId creditedAcc, long amount) implements AccountEvent {}

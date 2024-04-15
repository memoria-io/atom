package io.memoria.atom.tests.eventsourcing.event;

import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record CreditRejected(EventMeta meta, StateId debitedAcc, long amount) implements AccountEvent {}

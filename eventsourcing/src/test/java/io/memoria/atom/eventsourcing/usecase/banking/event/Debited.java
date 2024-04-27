package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record Debited(EventMeta meta, StateId creditedAcc, long amount) implements AccountEvent {}

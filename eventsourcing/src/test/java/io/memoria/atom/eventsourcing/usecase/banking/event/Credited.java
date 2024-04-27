package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record Credited(EventMeta meta, StateId debitedAcc, long amount) implements AccountEvent {}

package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;
import com.marmoush.atom.eventsourcing.state.StateId;

public record Debited(EventMeta meta, StateId creditedAcc, long amount) implements AccountEvent {}

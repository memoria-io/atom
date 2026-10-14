package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;
import com.marmoush.atom.eventsourcing.state.StateId;

public record Credited(EventMeta meta, StateId debitedAcc, long amount) implements AccountEvent {}

package com.marmoush.javakit.eventsourcing.usecase.banking.event;

import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.StateId;

public record Debited(EventMeta meta, StateId creditedAcc, long amount) implements AccountEvent {}

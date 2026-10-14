package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;

public record DebitRejected(EventMeta meta) implements AccountEvent {}

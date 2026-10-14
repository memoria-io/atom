package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;

public record DebitConfirmed(EventMeta meta) implements AccountEvent {}

package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;

public record AccountCreated(EventMeta meta, String name, long balance) implements AccountEvent {}

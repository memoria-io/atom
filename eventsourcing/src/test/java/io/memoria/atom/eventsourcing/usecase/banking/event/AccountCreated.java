package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record AccountCreated(EventMeta meta, String name, long balance) implements AccountEvent {}

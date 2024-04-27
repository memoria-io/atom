package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record AccountClosed(EventMeta meta) implements AccountEvent {}

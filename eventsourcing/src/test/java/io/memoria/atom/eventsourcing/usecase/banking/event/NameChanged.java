package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record NameChanged(EventMeta meta, String newName) implements AccountEvent {}

package io.memoria.atom.tests.eventsourcing.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record AccountCreated(EventMeta meta, String name, long balance) implements AccountEvent {}

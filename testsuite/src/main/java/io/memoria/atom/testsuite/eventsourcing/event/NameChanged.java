package io.memoria.atom.testsuite.eventsourcing.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record NameChanged(EventMeta meta, String newName) implements AccountEvent {}

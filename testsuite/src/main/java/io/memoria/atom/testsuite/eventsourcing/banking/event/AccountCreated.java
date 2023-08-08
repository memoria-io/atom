package io.memoria.atom.testsuite.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.EventMeta;

public record AccountCreated(EventMeta meta, String name, long balance) implements AccountEvent {}
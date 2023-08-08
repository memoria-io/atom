package io.memoria.atom.testsuite.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.EventMeta;

public record DebitRejected(EventMeta meta) implements AccountEvent {}

package io.memoria.atom.testsuite.eventsourcing.event;

import io.memoria.atom.eventsourcing.EventMeta;

public record DebitConfirmed(EventMeta meta) implements AccountEvent {}
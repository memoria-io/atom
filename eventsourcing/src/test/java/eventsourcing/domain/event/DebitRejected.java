package eventsourcing.domain.event;

import io.memoria.atom.eventsourcing.event.EventMeta;

public record DebitRejected(EventMeta meta) implements AccountEvent {}

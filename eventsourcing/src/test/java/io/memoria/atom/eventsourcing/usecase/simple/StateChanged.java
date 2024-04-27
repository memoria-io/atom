package io.memoria.atom.eventsourcing.usecase.simple;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;

public record StateChanged(EventMeta meta) implements Event {}

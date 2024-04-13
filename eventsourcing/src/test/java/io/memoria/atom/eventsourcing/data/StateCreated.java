package io.memoria.atom.eventsourcing.data;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;

public record StateCreated(EventMeta meta) implements Event {}

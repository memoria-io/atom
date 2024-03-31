package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;

public record StateCreated(EventMeta meta) implements Event {}

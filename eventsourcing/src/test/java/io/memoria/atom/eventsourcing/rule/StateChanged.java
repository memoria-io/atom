package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;

public record StateChanged(EventMeta meta) implements Event {}

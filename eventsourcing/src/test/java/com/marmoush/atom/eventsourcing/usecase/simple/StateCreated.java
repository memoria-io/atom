package com.marmoush.atom.eventsourcing.usecase.simple;

import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.event.EventMeta;

public record StateCreated(EventMeta meta) implements Event {}

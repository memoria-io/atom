package com.marmoush.javakit.eventsourcing.usecase.simple;

import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.event.EventMeta;

public record StateCreated(EventMeta meta) implements Event {}

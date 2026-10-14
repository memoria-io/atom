package com.marmoush.javakit.eventsourcing.usecase.banking.event;

import com.marmoush.javakit.eventsourcing.event.EventMeta;

public record ClosureRejected(EventMeta meta) implements AccountEvent {}

package com.marmoush.javakit.eventsourcing.usecase.banking.event;

import com.marmoush.javakit.eventsourcing.event.EventMeta;

public record NameChanged(EventMeta meta, String newName) implements AccountEvent {}

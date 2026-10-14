package com.marmoush.javakit.eventsourcing.usecase.banking.event;

import com.marmoush.javakit.eventsourcing.event.EventMeta;

public record AccountCreated(EventMeta meta, String name, long balance) implements AccountEvent {}

package com.marmoush.javakit.eventsourcing.usecase.banking.event;

import com.marmoush.javakit.eventsourcing.event.EventMeta;

public record DebitRejected(EventMeta meta) implements AccountEvent {}

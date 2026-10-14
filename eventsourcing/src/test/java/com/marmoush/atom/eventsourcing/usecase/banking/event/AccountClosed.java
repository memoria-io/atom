package com.marmoush.atom.eventsourcing.usecase.banking.event;

import com.marmoush.atom.eventsourcing.event.EventMeta;

public record AccountClosed(EventMeta meta) implements AccountEvent {}

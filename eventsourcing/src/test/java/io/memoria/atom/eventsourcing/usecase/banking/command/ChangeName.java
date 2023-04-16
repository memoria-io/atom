package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.core.id.Id;

public record ChangeName(Id accountId, Id commandId, String name) implements AccountCommand {}

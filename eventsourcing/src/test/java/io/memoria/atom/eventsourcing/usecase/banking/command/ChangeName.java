package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;

public record ChangeName(StateId accountId, CommandId commandId, String name) implements AccountCommand {}

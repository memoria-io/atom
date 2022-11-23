package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record ChangeName(StateId accountId, CommandId commandId, String name) implements AccountCommand {}

package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;

public record ChangeName(CommandMeta meta, String name) implements AccountCommand {}

package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;

public record CloseAccount(CommandMeta meta) implements AccountCommand {}

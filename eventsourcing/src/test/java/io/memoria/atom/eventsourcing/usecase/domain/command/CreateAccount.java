package io.memoria.atom.eventsourcing.usecase.domain.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;

public record CreateAccount(CommandMeta meta, String accountName, long balance) implements AccountCommand {}

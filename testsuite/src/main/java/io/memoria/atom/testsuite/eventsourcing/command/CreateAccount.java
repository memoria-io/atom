package io.memoria.atom.testsuite.eventsourcing.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;

public record CreateAccount(CommandMeta meta, String accountName, long balance) implements AccountCommand {}

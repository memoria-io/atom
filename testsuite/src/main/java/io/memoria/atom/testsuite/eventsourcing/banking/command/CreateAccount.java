package io.memoria.atom.testsuite.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandMeta;

public record CreateAccount(CommandMeta meta, String accountName, long balance) implements AccountCommand {}
